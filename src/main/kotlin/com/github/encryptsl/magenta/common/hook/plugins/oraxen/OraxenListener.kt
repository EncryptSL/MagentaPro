package com.github.encryptsl.magenta.common.hook.oraxen

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.hook.model.PluginHook
import io.th0rgal.oraxen.api.OraxenItems
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class OraxenListener(private val magenta: Magenta) : PluginHook("Oraxen"), Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    fun onOraxenItemsBlockBreak(event: BlockBreakEvent) {
        val player = event.player
        val worldName = player.location.world.name
        val itemInHand = player.inventory.itemInMainHand

        val oraxenItemId = OraxenItems.getIdByItem(itemInHand) ?: return
        if (!magenta.oraxenControl.getConfig().contains("oraxen.items")) return
        if (!magenta.oraxenControl.getConfig().contains("oraxen.items.$oraxenItemId")) return

        if (magenta.oraxenControl.getConfig().getBoolean("oraxen.items.$oraxenItemId.enabled")) {
            val whitelistedWorlds = magenta.oraxenControl.getConfig().getStringList("oraxen.items.$oraxenItemId.whitelisted-worlds")
            if (whitelistedWorlds.contains(worldName)) return
            event.isCancelled = true
        }
    }

}