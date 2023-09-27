package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.teleport.TpaDenyEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class TpaDenyListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onTpaDeny(event: TpaDenyEvent) {
        val sender = event.sender
        magenta.tpaManager.denyRequest(sender)
    }

}