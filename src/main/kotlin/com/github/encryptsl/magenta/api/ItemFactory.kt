package com.github.encryptsl.magenta.api

import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack

class ItemFactory {

    fun item(material: Material, amount: Int): ItemStack {
        val itemStack = ItemStack(material, amount)
        val itemMeta = itemStack.itemMeta
        itemMeta.displayName(ModernText.miniModernText(material.name))
        itemStack.setItemMeta(itemMeta)

        return itemStack
    }

    fun item(material: Material, amount: Int, enchant: Enchantment, level: Int): ItemStack {
        val itemStack = ItemStack(material, amount)
        val itemMeta = itemStack.itemMeta
        itemMeta.displayName(ModernText.miniModernText(material.name))
        itemStack.setItemMeta(itemMeta)
        itemStack.addEnchantment(enchant, level)

        return itemStack
    }

    fun item(material: Material, amount: Int, enchant: Enchantment, level: Int, lore: List<Component>): ItemStack {
        val itemStack = ItemStack(material, amount)
        val itemMeta = itemStack.itemMeta
        itemMeta.displayName(ModernText.miniModernText(material.name))
        itemStack.setItemMeta(itemMeta)
        itemStack.addEnchantment(enchant, level)

        if (lore.isNotEmpty()) {
            itemMeta.lore(lore)
        }

        return itemStack
    }
}