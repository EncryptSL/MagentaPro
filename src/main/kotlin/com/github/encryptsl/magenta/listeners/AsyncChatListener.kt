package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.jail.JailPlayerEvent
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener


class AsyncChatListener(private val magenta: Magenta) : Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    fun chat(event: AsyncChatEvent) {
        val player = event.player

        magenta.schedulerMagenta.runTask(magenta) {
            val jailPlayerEvent = JailPlayerEvent(player, "psát !")
            magenta.pluginManager.callEvent(jailPlayerEvent)
            if (jailPlayerEvent.isCancelled) {
                player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BASS, 1.25f, 1.25f)
                event.isCancelled = true
            }
        }
    }
}