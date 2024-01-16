package com.github.encryptsl.magenta.api.shop

import org.bukkit.event.inventory.InventoryClickEvent

interface ShopAction {
    fun buy(shopPaymentInformation: ShopPaymentInformation, isItem: Boolean, commands: MutableList<String>?, inventory: InventoryClickEvent)
    fun sell(shopPaymentInformation: ShopPaymentInformation, inventory: InventoryClickEvent)
}