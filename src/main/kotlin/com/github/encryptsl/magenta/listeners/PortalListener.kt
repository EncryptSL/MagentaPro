package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.Permissions
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.world.PortalCreateEvent

class PortalListener(private val magenta: Magenta) : Listener {

    private val typePortal = mapOf(
        "NETHER_PAIR" to "nether",
        "FIRE" to "nether",
        "END_PLATFORM" to "end"
    )

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPortalListener(event: PortalCreateEvent) {
        if (event.entity is Player) {
            val player = event.entity as Player
            if (!magenta.stringUtils.inInList("level.locked-progress.worlds", player.world.name)) return
            if (!magenta.config.getBoolean("level.locked-progress.portal.enabled")) return

            if (player.gameMode == GameMode.CREATIVE) return
            if (player.hasPermission(Permissions.PORTAL_BLACKLIST_BYPASS)) return

            magenta.levelAPI.getUserByUUID(player.uniqueId).thenApply {
                val level = magenta.config.getInt("level.locked-progress.portal.blacklist.${event.reason.name}")
                if (level > it.level) {
                    player.sendMessage(magenta.locale.translation("magenta.portal.error", TagResolver.resolver(
                        Placeholder.parsed("dimension", typePortal[event.reason.name].toString()),
                        Placeholder.parsed("level", level.toString()),
                    )))
                    event.isCancelled = true
                }
            }.exceptionally {
                event.isCancelled = false
            }
        }
    }
}