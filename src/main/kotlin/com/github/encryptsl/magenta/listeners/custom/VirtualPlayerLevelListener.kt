package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.level.VirtualLevelUpEvent
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class VirtualPlayerLevelListener(private val magenta: Magenta) : Listener {
    @EventHandler
    fun onLevelUp(event: VirtualLevelUpEvent) {
        val offlinePlayer = event.offlinePlayer
        val level = event.newLevel
        val currentExp = event.currentExp
        val expToLevel = event.experienceToLevel

        magenta.virtualLevel.addLevel(offlinePlayer.uniqueId, 1)
            offlinePlayer.player?.let {
                it.playSound(it.location,
                    Sound.valueOf(magenta.config.getString("level.effect.sound").toString()),
                    magenta.config.getString("level.effect.volume").toString().toFloat(),
                    magenta.config.getString("level.effect.pitch").toString().toFloat()
                )
                magenta.config.getStringList("level-up-format").forEach { line ->
                    it.sendMessage(
                        ModernText.miniModernTextCenter(
                            line, TagResolver.resolver(
                                Placeholder.parsed("level", level.toString()),
                                Placeholder.parsed("current_exp", currentExp.toString()),
                                Placeholder.parsed("exp_to_level", expToLevel.toString()),
                            )
                        )
                    )
                }
            }
        magenta.virtualLevel.setExperience(offlinePlayer.uniqueId, 0)
    }

}