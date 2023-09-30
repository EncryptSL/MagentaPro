package com.github.encryptsl.magenta.common.tasks

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.JailManager
import com.github.encryptsl.magenta.api.PlayerAccount
import com.github.encryptsl.magenta.api.events.jail.JailPardonEvent
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Sound

class JailCountDownTask(private val magenta: Magenta) : Runnable {
    override fun run() {
        magenta.server.onlinePlayers.forEach { player ->
            val jailManager = JailManager(magenta, player.uniqueId)
            val timeLeft = jailManager.remainingTime(player)
            if (jailManager.hasPunish(player)) {
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 1.15f, 1.15f)
                player.sendActionBar(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.jail.success.remaining"), TagResolver.resolver(
                    Placeholder.parsed("remaining", timeLeft.toString())
                )))
            }
        }
    }
}