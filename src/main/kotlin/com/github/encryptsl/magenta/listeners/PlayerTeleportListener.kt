package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.magenta.Magenta
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTeleportEvent

class PlayerTeleportListener(private val magenta: Magenta) : Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerTeleport(event: PlayerTeleportEvent) {
        val player = event.player
        val user = magenta.user.getUser(player.uniqueId)
        if (user.isJailed()) return
        user.saveLastLocation(player)
    }

}