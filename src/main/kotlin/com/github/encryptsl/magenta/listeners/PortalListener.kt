package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.magenta.Magenta
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.world.PortalCreateEvent
import org.bukkit.event.world.PortalCreateEvent.CreateReason

class PortalListener(private val magenta: Magenta) : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun onPortalListener(event: PortalCreateEvent) {
        if (event.entity is Player) {
            val player = event.entity as Player
            if (player.gameMode == GameMode.CREATIVE) return
            if (player.hasPermission("magenta.portal.blacklist.bypass")) return
            val reason: CreateReason = event.reason

            if (!magenta.stringUtils.inInList("portal.blacklist", reason.name)) return

            player.sendMessage(magenta.localeConfig.translation("magenta.portal.error"))
            event.isCancelled = true
        }
    }
}