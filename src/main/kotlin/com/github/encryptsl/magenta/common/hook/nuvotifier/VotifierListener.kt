package com.github.encryptsl.magenta.common.hook.nuvotifier

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.vote.VotePartyPlayerStartedEvent
import com.github.encryptsl.magenta.common.database.entity.VoteEntity
import com.vexsoftware.votifier.model.Vote
import com.vexsoftware.votifier.model.VotifierEvent
import com.vexsoftware.votifier.util.QuietException
import kotlinx.datetime.Instant
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class VotifierListener(private val magenta: Magenta) : Listener {
    @EventHandler(priority = EventPriority.NORMAL)
    fun onVote(event: VotifierEvent) {
        val vote: Vote = event.vote
        val username = vote.username
        val timestamp = vote.timeStamp
        val serviceName = VoteHelper.replaceService(vote.serviceName, ".", "_")
        val player = Bukkit.getOfflinePlayer(username)
        if (!player.hasPlayedBefore() || username.isNullOrEmpty()) return
        try {
            if (magenta.config.contains("votifier.sound")) {
                VoteHelper.playSoundForAll(
                    Sound.valueOf(magenta.config.getString("votifier.sound").toString()),
                    magenta.config.getString("votifier.volume").toString().toFloat(),
                    magenta.config.getString("votifier.pitch").toString().toFloat()
                )
            }

            if (magenta.config.contains("votifier.services.$serviceName")) {
                processVote(serviceName, player, timestamp.toLong())
            } else {
                processDefaultReward(serviceName, player, timestamp.toLong())
            }

            if (magenta.config.contains("votifier.cumulative")) {
                processCumulativeVote(serviceName, player)
            }

            if (magenta.config.contains("votifier.voteparty")) {
                if (!magenta.config.getBoolean("votifier.voteparty.enabled")) return
                if (!magenta.config.contains("votifier.voteparty.countdown")) return

                magenta.voteParty.updateParty()
                checkVoteParty(player)
            }
        } catch (e : QuietException) {
            magenta.logger.severe(e.message ?: e.localizedMessage)
        }
    }

    private fun processVote(serviceName: String, player: OfflinePlayer, timestamp: Long) {
        if (magenta.config.contains("votifier.services.$serviceName.rewards")) {
            val voteEntity = VoteEntity(player.name.toString(), player.uniqueId, 1, VoteHelper.replaceService(serviceName, "_", "."), Instant.fromEpochMilliseconds(timestamp))
            magenta.vote.addVote(voteEntity)
            VoteHelper.broadcast(
                magenta.localeConfig.getMessage("magenta.votifier.broadcast"),
                player.name.toString(),
                serviceName
            )
            val rewards: MutableList<String> = magenta.config.getStringList("votifier.services.$serviceName.rewards")
            if (!player.isOnline) {
                VoteHelper.saveOfflineReward(magenta, player, rewards)
                return
            }
            VoteHelper.giveRewards(rewards, player.name.toString())
        }
    }

    private fun processDefaultReward(serviceName: String, player: OfflinePlayer, timestamp: Long) {
        if (magenta.config.contains("votifier.services.default")) {
            if (magenta.config.contains("votifier.services.default.rewards")) {
                val voteEntity = VoteEntity(player.name.toString(), player.uniqueId, 1, VoteHelper.replaceService(serviceName, "_", "."), Instant.fromEpochMilliseconds(timestamp))
                magenta.vote.addVote(voteEntity)
                VoteHelper.broadcast(
                    magenta.localeConfig.getMessage("magenta.votifier.broadcast"),
                    player.name.toString(),
                    serviceName
                )
                val rewards: MutableList<String> = magenta.config.getStringList("votifier.services.default.rewards")
                if (!player.isOnline) {
                    VoteHelper.saveOfflineReward(magenta, player, rewards)
                    return
                }

                VoteHelper.giveRewards(rewards, player.name.toString())
                magenta.logger.severe("Service for vote $serviceName not set in config.yml")
            }
        }
    }

    private fun processCumulativeVote(serviceName: String, player: OfflinePlayer) {
        if (magenta.config.contains("votifier.cumulative.${magenta.vote.getPlayerVote(player.uniqueId)}")) {
            if (magenta.config.contains("votifier.cumulative.${magenta.vote.getPlayerVote(player.uniqueId)}.broadcast")) {
                VoteHelper.broadcast(magenta.config.getString("votifier.cumulative.${magenta.vote.getPlayerVote(player.uniqueId)}.broadcast").toString(),
                    player.name.toString(), serviceName
                )
            }
            val rewards: MutableList<String> = magenta.config.getStringList("votifier.cumulative.${magenta.vote.getPlayerVote(player.uniqueId)}.rewards")
            if (!player.isOnline) {
                VoteHelper.saveOfflineReward(magenta, player, rewards)
                return
            }
            VoteHelper.giveRewards(rewards, player.name.toString())
        }
    }

    private fun checkVoteParty(player: OfflinePlayer) {
        if (magenta.config.contains("votifier.voteparty.rewards")) {
            if (magenta.voteParty.getVoteParty().currentVotes == magenta.config.getInt("votifier.voteparty.start_at")) {
                val rewards: MutableList<String> = magenta.config.getStringList("votifier.voteparty.rewards")
                magenta.pluginManager.callEvent(VotePartyPlayerStartedEvent(player.name.toString()))
                VoteHelper.startVoteParty(
                    magenta,
                    magenta.localeConfig.getMessage("magenta.votifier.voteparty.broadcast"),
                    magenta.localeConfig.getMessage("magenta.votifier.voteparty.success"),
                    rewards,
                    magenta.config.getInt("votifier.voteparty.countdown"),
                )
            }
        }
    }

}