package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.kit.KitAdminGiveEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class KitAdminGiveListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onKitAdminGive(event: KitAdminGiveEvent) {
        val commandSender = event.commandSender
        val target = event.target
        val kitManager = event.kitManager

    }

}