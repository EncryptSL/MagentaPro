package com.github.encryptsl.magenta.common.hook.nuvotifier

import com.github.encryptsl.kmono.lib.api.hook.PluginHook
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.vote.VotePartyPlayerStartedEvent
import com.github.encryptsl.magenta.common.database.entity.VoteEntity
import com.vexsoftware.votifier.model.Vote
import com.vexsoftware.votifier.model.VotifierEvent
import kotlinx.datetime.Instant
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class VotifierListener(private val magenta: Magenta) : PluginHook("Votifier"), Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    fun onVote(event: VotifierEvent) {
        val vote: Vote = event.vote
        val username = vote.username
        val timestamp = vote.timeStamp
        val serviceName = VoteHelper.replaceService(vote.serviceName, ".", "_")
        val player = Bukkit.getOfflinePlayer(username)
        try {
            if (!player.hasPlayedBefore() || username.isNullOrEmpty())
                throw Exception("VotifierEvent security exception catch null username or player never join server.")

            if (magenta.config.contains("votifier.sound")) {
                VoteHelper.playSoundForAll(
                    magenta.config.getString("votifier.sound").toString(),
                    magenta.config.getString("votifier.volume").toString().toFloat(),
                    magenta.config.getString("votifier.pitch").toString().toFloat()
                )
            }

            if (magenta.config.contains("votifier.services.$serviceName")) {
                processVote(serviceName, player, timestamp.toLong())
            } else {
                processDefaultReward(serviceName, player, timestamp.toLong())
            }

            if (magenta.config.contains("votifier.cumulative") && !magenta.config.getBoolean("votifier.disable-cumulative-rewards")) {
                processCumulativeVote(serviceName, player)
            }

            if (magenta.config.contains("votifier.voteparty")) {
                if (!magenta.config.getBoolean("votifier.voteparty.enabled")) return
                if (!magenta.config.contains("votifier.voteparty.countdown")) return

                magenta.voteParty.updateParty()
                checkVoteParty(player)
            }
        } catch (e : Exception) {
            magenta.logger.severe(e.message ?: e.localizedMessage)
        }
    }

    private fun processVote(serviceName: String, player: OfflinePlayer, timestamp: Long) {
        if (magenta.config.contains("votifier.services.$serviceName.rewards")) {
            addVote(player, serviceName, timestamp)
            VoteHelper.broadcast(magenta.locale, "magenta.votifier.broadcast", player.name.toString(), serviceName)

            val rewards: MutableList<String> = magenta.config.getStringList("votifier.services.$serviceName.rewards")
            val expressionFormula = expressionFormula(player)
            if (!player.isOnline)
                return VoteHelper.saveOfflineReward(magenta, player, rewards, expressionFormula)

            VoteHelper.giveRewards(rewards, player.name.toString(), expressionFormula)
        }
    }

    private fun processDefaultReward(serviceName: String, player: OfflinePlayer, timestamp: Long) {
        if (!magenta.config.contains("votifier.services.default")) return

        if (magenta.config.contains("votifier.services.default.rewards")) {
            addVote(player, serviceName, timestamp)
            VoteHelper.broadcast(magenta.locale, "magenta.votifier.broadcast", player.name.toString(), serviceName)
            val rewards: MutableList<String> = magenta.config.getStringList("votifier.services.default.rewards")
            val expressionFormula = expressionFormula(player)
            if (!player.isOnline)
                return VoteHelper.saveOfflineReward(magenta, player, rewards, expressionFormula)

            VoteHelper.giveRewards(rewards, player.name.toString(), expressionFormula)
            magenta.logger.severe("Service for vote $serviceName not set in config.yml")
        }
    }

    private fun processCumulativeVote(serviceName: String, player: OfflinePlayer) {
        val playerVotes = magenta.vote.getUserVotesByUUID(player.uniqueId).join()

        if (!magenta.config.contains("votifier.cumulative.${playerVotes}")) return

        if (magenta.config.contains("votifier.cumulative.${playerVotes}.broadcast")) {
            VoteHelper.broadcast(magenta.locale, "votifier.cumulative.$playerVotes.broadcast", player.name.toString(), serviceName)
        }
        val rewards: MutableList<String> = magenta.config.getStringList("votifier.cumulative.${playerVotes}.rewards")
        val expressionFormula = expressionFormula(player, playerVotes)

        if (!player.isOnline)
            return VoteHelper.saveOfflineReward(magenta, player, rewards, expressionFormula)

        VoteHelper.giveRewards(rewards, player.name.toString(), expressionFormula)
    }

    private fun checkVoteParty(player: OfflinePlayer) {
        if (!magenta.config.contains("votifier.voteparty.rewards")) return

        magenta.voteParty.getVoteParty().thenAccept {
            if (it.currentVotes == magenta.config.getInt("votifier.voteparty.start_at")) {
                val rewards: MutableList<String> = magenta.config.getStringList("votifier.voteparty.rewards")
                magenta.pluginManager.callEvent(VotePartyPlayerStartedEvent(player.name.toString()))
                VoteHelper.startVoteParty(magenta, rewards)
                magenta.logger.info("DEBUG: VoteParty started")
            }
        }
    }

    private fun addVote(p: OfflinePlayer, serviceName: String, timestamp: Long) {
        magenta.vote.addVote(VoteEntity(
            p.name.toString(), p.uniqueId, 1, VoteHelper.replaceService(serviceName, "_", "."), Instant.fromEpochMilliseconds(timestamp)
        ))
    }

    private fun expressionFormula(player: OfflinePlayer, value: Int = 0): String {
        return magenta.stringUtils.arithmeticExpression(player, magenta.config, "votifier.expression-formula", value)
    }
}