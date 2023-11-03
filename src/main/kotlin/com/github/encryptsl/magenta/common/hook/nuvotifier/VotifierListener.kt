package com.github.encryptsl.magenta.common.hook.nuvotifier

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.vote.VotePartyEvent
import com.github.encryptsl.magenta.common.database.entity.VoteEntity
import com.github.encryptsl.magenta.common.extensions.datetime
import com.vexsoftware.votifier.model.Vote
import com.vexsoftware.votifier.model.VotifierEvent
import kotlinx.datetime.Instant
import org.bukkit.Bukkit
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
        if (!player.hasPlayedBefore()) return

        if (magenta.config.contains("votifier.sound")) {
            VoteHelper.playSoundForAll(
                Sound.valueOf(magenta.config.getString("votifier.sound").toString()),
                magenta.config.getString("votifier.volume").toString().toFloat(),
                magenta.config.getString("votifier.pitch").toString().toFloat()
            )
        }

        if (!magenta.config.contains("votifier.services.$serviceName")) {
            if (magenta.config.contains("votifier.services.default")) {
                if (magenta.config.contains("votifier.services.default.rewards")) {
                    val voteEntity = VoteEntity(player.name.toString(), player.uniqueId, 1, VoteHelper.replaceService(serviceName, "_", "."), Instant.fromEpochMilliseconds(timestamp.toLong()))
                    magenta.vote.addVote(voteEntity)
                    VoteHelper.broadcast(
                        magenta.localeConfig.getMessage("magenta.votifier.broadcast"),
                        username,
                        serviceName
                    )
                    val rewards: MutableList<String> = magenta.config.getStringList("votifier.services.default.rewards")
                    if (!player.isOnline) {
                        VoteHelper.saveOfflineReward(magenta, player, rewards)
                        return
                    }

                    VoteHelper.giveRewards(rewards, username)
                }
            }
            magenta.logger.severe("Service for vote $serviceName not set in config.yml")
        }

        if (magenta.config.contains("votifier.services.$serviceName")) {
            if (magenta.config.contains("votifier.services.$serviceName.rewards")) {
                val voteEntity = VoteEntity(player.name.toString(), player.uniqueId, 1, VoteHelper.replaceService(serviceName, "_", "."), Instant.fromEpochMilliseconds(timestamp.toLong()))
                magenta.vote.addVote(voteEntity)
                VoteHelper.broadcast(
                    magenta.localeConfig.getMessage("magenta.votifier.broadcast"),
                    username,
                    serviceName
                )
                val rewards: MutableList<String> = magenta.config.getStringList("votifier.services.$serviceName.rewards")
                if (!player.isOnline) {
                    VoteHelper.saveOfflineReward(magenta, player, rewards)
                    return
                }
                VoteHelper.giveRewards(rewards, username)
            }
        }

        if (magenta.config.contains("votifier.cumulative")) {
            if (magenta.config.contains("votifier.cumulative.${magenta.vote.getPlayerVote(player.uniqueId)}")) {
                if (magenta.config.contains("votifier.cumulative.${magenta.vote.getPlayerVote(player.uniqueId)}.broadcast")) {
                    VoteHelper.broadcast(
                        magenta.config.getString("votifier.cumulative.${magenta.vote.getPlayerVote(player.uniqueId)}.broadcast").toString(),
                        username, serviceName
                    )
                }
                val rewards: MutableList<String> = magenta.config.getStringList("votifier.cumulative.${magenta.vote.getPlayerVote(player.uniqueId)}.rewards")
                if (!player.isOnline) {
                    VoteHelper.saveOfflineReward(magenta, player, rewards)
                    return
                }
                VoteHelper.giveRewards(rewards, username)
            }
        }

        if (magenta.config.contains("votifier.voteparty")) {
            if (!magenta.config.getBoolean("votifier.voteparty.enabled")) return
            if (!magenta.config.contains("votifier.voteparty.countdown")) return

            VoteHelper.setVotePartyVote(magenta, magenta.config.getInt("votifier.voteparty.current_votes") + 1)

            if (magenta.config.contains("votifier.voteparty.rewards")) {
                if (magenta.vote.getVotesForParty() == magenta.config.getInt("votifier.voteparty.start_at")) {
                    val rewards: MutableList<String> = magenta.config.getStringList("votifier.voteparty.rewards")
                    magenta.pluginManager.callEvent(VotePartyEvent(username, Bukkit.getOnlinePlayers().size, datetime()))
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

}