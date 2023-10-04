package com.github.encryptsl.magenta.common.hook.nuvotifier

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ModernText
import com.vexsoftware.votifier.model.Vote
import com.vexsoftware.votifier.model.VotifierEvent
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class VotifierListener(private val magenta: Magenta) : Listener {

    private val count: MutableMap<String, Int> = HashMap()
    @EventHandler(priority = EventPriority.NORMAL)
    fun onVote(event: VotifierEvent) {
        val vote: Vote = event.vote
        val username = vote.username
        val timestamp = vote.timeStamp
        val serviceName = vote.serviceName.replace(".", "_")
        val address = vote.address
        count["votes"] = count.getOrDefault("votes", 0) + 1

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
                    VoteHelper.broadcast(magenta.localeConfig.getMessage("magenta.votifier.broadcast"), username, serviceName)

                    val rewards: MutableList<String> = magenta.config.getStringList("votifier.services.default.rewards")
                    VoteHelper.giveRewards(rewards, username)
                }
            }
            magenta.logger.severe("Service for vote $serviceName not set in config.yml")
        }

        if (magenta.config.contains("votifier.services.$serviceName.service")) {
            if (magenta.config.contains("votifier.services.$serviceName.rewards")) {
                VoteHelper.broadcast(magenta.localeConfig.getMessage("magenta.votifier.broadcast"), username, serviceName)
                val rewards: MutableList<String> = magenta.config.getStringList("votifier.services.$serviceName.rewards")
                VoteHelper.giveRewards(rewards, username)
            }
            Bukkit.broadcast(ModernText.miniModernText(count["votes"].toString()))
        }

        if (magenta.config.contains("votifier.cumulative")) {
            if (magenta.config.contains("votifier.cumulative.${count["votes"]}")) {
                if (magenta.config.contains("votifier.cumulative.${count["votes"]}.broadcast")) {
                    VoteHelper.broadcast(
                        magenta.config.getString("votifier.cumulative.${count["votes"]}.broadcast").toString(),
                        username, serviceName
                    )
                }

                val rewards: MutableList<String> = magenta.config.getStringList("votifier.cumulative.${count["votes"]}.rewards")
                VoteHelper.giveRewards(rewards, username)
            }
        }

        if (magenta.config.contains("votifier.voteparty")) {
            if (!magenta.config.getBoolean("votifier.voteparty.enabled")) return
            if (!magenta.config.contains("votifier.voteparty.countdown")) return
            if (magenta.config.contains("votifier.voteparty.rewards")) {
                if (magenta.config.getInt("votifier.voteparty.start_party") == 100) {
                    val rewards: MutableList<String> = magenta.config.getStringList("votifier.voteparty.rewards")
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