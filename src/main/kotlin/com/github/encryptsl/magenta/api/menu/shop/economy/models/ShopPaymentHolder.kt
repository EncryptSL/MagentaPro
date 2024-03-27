package com.github.encryptsl.magenta.api.menu.shop.economy.models

import org.bukkit.inventory.ItemStack

data class ShopPaymentHolder(val itemStack: ItemStack, val price: Double, val isOperationAllowed: Boolean)