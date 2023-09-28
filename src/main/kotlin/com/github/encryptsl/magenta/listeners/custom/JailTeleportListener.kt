package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.jail.JailTeleportEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class JailTeleportListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onJailTeleport(event: JailTeleportEvent) {
        val player = event.target
        val location = event.location

        magenta.schedulerMagenta.runTask(magenta) {
            player.teleport(location)
        }
    }

}