package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.teleport.TpaAcceptEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class TpaAcceptListener(private val magenta: Magenta) : Listener {


    @EventHandler
    fun onTpaAccept(event: TpaAcceptEvent) {
        val player = event.player
        magenta.tpaManager.acceptRequest(player)
    }

}