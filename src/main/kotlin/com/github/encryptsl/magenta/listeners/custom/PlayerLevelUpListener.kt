package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.kmono.lib.extensions.playSound
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.level.LevelUpEvent
import com.github.encryptsl.magenta.common.extensions.sendConsoleCommand
import com.github.encryptsl.magenta.common.utils.ActionTitleManager
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class PlayerLevelUpListener(private val magenta: Magenta) : Listener {
    @EventHandler
    fun onLevelUp(event: LevelUpEvent) {
        val offlinePlayer = event.offlinePlayer
        val level = event.newLevel.plus(1)
        val currentExp = event.currentExp
        val expToLevel = event.experienceToLevel

        if (magenta.config.getBoolean("level.level-up-progress.enabled")) {
            if (magenta.config.contains("level.level-up-progress.level-up-rewards.${level}")) {
                val broadcast = magenta.config.getString("level.level-up-progress.level-up-rewards.${level}.broadcast")
                val rewards = magenta.config.getStringList("level.level-up-progress.level-up-rewards.${level}.commands")

                broadcast?.let {
                    ActionTitleManager.sendBroadcast(Audience.audience(Bukkit.getOnlinePlayers()), ModernText.miniModernText(it, Placeholder.parsed("level", level.toString())))
                }
                for (reward in rewards) {
                    sendConsoleCommand(reward, offlinePlayer)
                }
            }
        }

        magenta.levelAPI.addLevel(offlinePlayer.uniqueId, 1)
        offlinePlayer.player?.let {
            playSound(it,
                magenta.config.getString("level.effect.sound").toString(),
                magenta.config.getString("level.effect.volume").toString().toFloat(),
                magenta.config.getString("level.effect.pitch").toString().toFloat()
            )
            it.sendMessage(ModernText.miniModernText(magenta.config.getString("level.format").toString(), TagResolver.resolver(
                Placeholder.parsed("level", level.toString()),
                Placeholder.parsed("current_exp", currentExp.toString()),
                Placeholder.parsed("exp_to_level", expToLevel.toString())
            )))
        }
        magenta.levelAPI.setExperience(offlinePlayer.uniqueId, 0)
    }

}