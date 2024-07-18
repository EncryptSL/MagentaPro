package com.github.encryptsl.magenta.common.tasks

import com.github.encryptsl.kmono.lib.extensions.formatFromSecondsTime
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.jail.JailPardonEvent
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Bukkit
import org.bukkit.Sound

class JailCountDownTask(private val magenta: Magenta) : Runnable {

    override fun run() {
        val jailedPlayers = Bukkit.getOnlinePlayers().filter { magenta.user.getUser(it.uniqueId).isJailed() }.map { magenta.user.getUser(it.uniqueId) }

        val iterator = jailedPlayers.iterator()
        while (iterator.hasNext()) {
            val user = iterator.next()
            val timeLeft = user.getRemainingJailTime()
            val player = user.getPlayer()
            if (user.hasPunish()) {
                if (timeLeft == 0L) {
                    magenta.pluginManager.callEvent(JailPardonEvent(user.getOfflinePlayer()))
                }
                user.setOnlineTime(timeLeft)
                player?.let { it.playSound(it, Sound.BLOCK_NOTE_BLOCK_BASS, 1.15f, 1.15f) }
                player?.sendActionBar(
                    magenta.locale.translation("magenta.command.jail.success.remaining",
                        Placeholder.parsed("remaining", formatFromSecondsTime(timeLeft)))
                )
            }
        }
    }
}