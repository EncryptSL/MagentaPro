package com.github.encryptsl.magenta.api.menu.shop

import org.bukkit.event.inventory.InventoryClickEvent

interface ShopAction {
    fun buy(shopPaymentInformation: ShopPaymentInformation, commands: MutableList<String>?, inventory: InventoryClickEvent)
    fun sell(shopPaymentInformation: ShopPaymentInformation, inventory: InventoryClickEvent)
}