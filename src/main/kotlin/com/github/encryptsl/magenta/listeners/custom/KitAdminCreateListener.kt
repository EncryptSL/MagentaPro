package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.api.events.kit.KitAdminCreateEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class KitAdminCreateListener : Listener {

    @EventHandler
    fun onCreateKit(event: KitAdminCreateEvent) {
        val kitName = event.kitName
    }

}