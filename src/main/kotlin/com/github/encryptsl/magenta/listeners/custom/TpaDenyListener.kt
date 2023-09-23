package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.teleport.TpaDenyEvent
import com.github.encryptsl.magenta.common.TpaRequestManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class TpaDenyListener(magenta: Magenta) : Listener {

    private val tpaRequestManager = TpaRequestManager(magenta)

    @EventHandler
    fun onTpaDeny(event: TpaDenyEvent) {
        val sender = event.sender
        tpaRequestManager.denyRequest(sender)
    }

}