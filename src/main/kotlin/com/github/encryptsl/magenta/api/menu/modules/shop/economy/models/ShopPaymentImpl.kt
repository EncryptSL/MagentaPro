package com.github.encryptsl.magenta.api.menu.modules.shop.economy.models

import org.bukkit.event.inventory.InventoryClickEvent

interface ShopPaymentImpl {
    fun buy(shopPaymentHolder: ShopPaymentHolder, commands: MutableList<String>?, inventory: InventoryClickEvent)
    fun sell(shopPaymentHolder: ShopPaymentHolder, inventory: InventoryClickEvent)
}