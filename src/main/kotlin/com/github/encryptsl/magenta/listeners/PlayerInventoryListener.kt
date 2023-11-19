package com.github.encryptsl.magenta.listeners

import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType

class PlayerInventoryListener : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerInventory(event: InventoryClickEvent) {
        val top = event.view.topInventory
        val type = top.type

        val whoClicked = event.whoClicked
        if (type == InventoryType.PLAYER) {
            val ownerInv = top.holder ?: return
            if (ownerInv is HumanEntity) {
                if (!whoClicked.hasPermission("magenta.invsee.modify") && ownerInv.hasPermission("magenta.invsee.prevent.modify")) {
                    if (whoClicked.hasPermission("magenta.invsee.prevent.modify.exempt")) return
                    event.isCancelled = true
                }
            }
        } else if (type == InventoryType.ENDER_CHEST) {
            if (!whoClicked.hasPermission("magenta.echest.modify")) {
                event.isCancelled = true
            }
        }
    }

}