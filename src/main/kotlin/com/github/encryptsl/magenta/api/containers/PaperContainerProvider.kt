package com.github.encryptsl.magenta.api.containers

import org.bukkit.entity.Player
import org.bukkit.inventory.InventoryView

class PaperContainerProvider : ContainerProvider {

    override fun openWorkBench(player: Player): InventoryView? {
        return player.openWorkbench(null, true)
    }
    override fun openAnvil(player: Player): InventoryView? {
        return player.openAnvil(null, true)
    }

    override fun openGrindStone(player: Player): InventoryView? {
        return player.openGrindstone(null, true)
    }

    override fun openStonecutter(player: Player): InventoryView? {
        return player.openWorkbench(null, true)
    }

    override fun openSmithingTable(player: Player): InventoryView? {
        return player.openSmithingTable(null, true)
    }

    override fun openLoom(player: Player): InventoryView? {
        return player.openSmithingTable(null, true)
    }
}