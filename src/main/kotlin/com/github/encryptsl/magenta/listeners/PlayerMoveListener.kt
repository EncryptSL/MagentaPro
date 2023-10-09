package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.magenta.Magenta
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class PlayerMoveListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        val player = event.player
        magenta.afk.addTime(player, magenta.config.getLong("auto-afk"))
    }

}