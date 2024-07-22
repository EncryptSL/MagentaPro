package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.Permissions
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.world.PortalCreateEvent

class PortalListener(private val magenta: Magenta) : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun onPortalListener(event: PortalCreateEvent) {
        if (event.entity is Player) {
            val player = event.entity as Player
            if (!magenta.config.getBoolean("portal.enabled")) return
            if (player.gameMode == GameMode.CREATIVE) return
            if (player.hasPermission(Permissions.PORTAL_BLACKLIST_BYPASS)) return

            if (!magenta.stringUtils.inInList("portal.blacklist", event.reason.name)) return

            player.sendMessage(magenta.locale.translation("magenta.portal.error"))
            event.isCancelled = true
        }
    }
}