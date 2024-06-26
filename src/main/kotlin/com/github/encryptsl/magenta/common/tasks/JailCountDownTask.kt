package com.github.encryptsl.magenta.common.tasks

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.jail.JailPardonEvent
import com.github.encryptsl.magenta.common.extensions.formatFromSecondsTime
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Bukkit
import org.bukkit.Sound

class JailCountDownTask(private val magenta: Magenta) : Runnable {

    override fun run() {
        for (player in Bukkit.getOnlinePlayers()) {
            val account = magenta.user.getUser(player.uniqueId)
            val timeLeft = account.getRemainingJailTime()
            if (account.hasPunish()) {
                if (timeLeft == 0L) {
                    magenta.pluginManager.callEvent(JailPardonEvent(player))
                }
                account.setOnlineTime(timeLeft)
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 1.15f, 1.15f)
                player.sendActionBar(magenta.locale.translation("magenta.command.jail.success.remaining",
                    Placeholder.parsed("remaining", formatFromSecondsTime(timeLeft))
                ))
            }
        }
    }
}