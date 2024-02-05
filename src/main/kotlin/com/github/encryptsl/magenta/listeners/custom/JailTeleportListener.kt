package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.jail.JailTeleportEvent
import com.github.encryptsl.magenta.api.scheduler.SchedulerMagenta
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class JailTeleportListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onJailTeleport(event: JailTeleportEvent) {
        val player = event.target
        val location = event.location

        SchedulerMagenta.doSync(magenta) {
            player.teleport(location)
        }
    }

}