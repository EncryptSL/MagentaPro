package com.github.encryptsl.magenta.api.containers

import org.bukkit.entity.Player
import org.bukkit.inventory.InventoryView

interface ContainerProvider {

    fun openWorkBench(player: Player): InventoryView?
    fun openAnvil(player: Player): InventoryView?
    fun openGrindStone(player: Player): InventoryView?
    fun openStonecutter(player: Player): InventoryView?
    fun openSmithingTable(player: Player): InventoryView?
    fun openLoom(player: Player): InventoryView?
}