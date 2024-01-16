package com.github.encryptsl.magenta.api.shop

import org.bukkit.inventory.ItemStack

data class ShopPaymentInformation(val itemStack: ItemStack, val price: Double, val isOperationAllowed: Boolean, )