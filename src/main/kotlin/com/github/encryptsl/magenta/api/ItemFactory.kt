package com.github.encryptsl.magenta.api

import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
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

    fun item(material: Material, amount: Int, itemName: String, sid: Int, lores: List<String>, glowing: Boolean): ItemStack {
        val itemStack = ItemStack(material, amount)
        val itemMeta = itemStack.itemMeta
        itemMeta.displayName(ModernText.miniModernText(itemName, Placeholder.parsed("sid", sid.toString())))

        if (glowing) {
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            itemMeta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true)
        }

        if (lores.isNotEmpty()) {
            val newList: MutableList<Component> = ArrayList()
            for (loreItem in lores) {
                newList.add(ModernText.miniModernText(loreItem))
            }
            itemMeta.lore(newList)
        }
        itemStack.setItemMeta(itemMeta)

        return itemStack
    }
}