package com.github.encryptsl.magenta.common.tasks

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.PlayerAccount
import com.github.encryptsl.magenta.api.events.jail.JailPardonEvent
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Sound
import java.time.Duration

class JailCountDownTask(private val magenta: Magenta) : Runnable {
    override fun run() {
        magenta.server.onlinePlayers.forEach { player ->
            val playerAccount = PlayerAccount(magenta, player.uniqueId)
            val timeLeft: Duration = playerAccount.cooldownManager.getRemainingCooldown("jail")
            if (playerAccount.cooldownManager.hasCooldown("jail") || playerAccount.getAccount().getBoolean("jailed")) {
                if (timeLeft.seconds == 0L) {
                    magenta.schedulerMagenta.runTask(magenta) {
                        magenta.pluginManager.callEvent(JailPardonEvent(player))
                    }
                    return
                }
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 1.15f, 1.15f)
                player.sendActionBar(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.jail.success.remaining"), TagResolver.resolver(
                    Placeholder.parsed("remaining", timeLeft.seconds.toString())
                )))
            }
        }
    }
}