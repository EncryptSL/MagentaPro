package com.github.encryptsl.magenta.api.menu.modules.shop

import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemStack

data class Product(
    val itemStack: ItemStack,
    val displayName: Component,
    val productPrice: Double,
    val amount: Int
)
