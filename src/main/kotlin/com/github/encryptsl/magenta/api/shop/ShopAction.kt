package com.github.encryptsl.magenta.api.shop

import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

interface ShopAction {
    fun buy(item: ItemStack, isBuyAllowed: Boolean, price: Double, inventory: InventoryClickEvent)
    fun sell(item: ItemStack, isSellAllowed: Boolean, price: Double, inventory: InventoryClickEvent)
}