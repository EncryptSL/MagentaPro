package com.github.encryptsl.magenta.api

import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
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

    fun shopItem(material: Material, buyPrice:Double, sellPrice:Double, lore: List<String>): ItemStack {
        val itemStack = ItemStack(material, 1)
        val itemMeta = itemStack.itemMeta
        if (lore.isNotEmpty()) {
            val newList: MutableList<Component> = ArrayList()
            for (loreItem in lore) {
                newList.add(ModernText.miniModernText(loreItem, TagResolver.resolver(
                    Placeholder.parsed("price", buyPrice.toString()),
                    Placeholder.parsed("sell", sellPrice.toString()),
                )))
            }
            itemMeta.lore(newList)
        }
        itemStack.setItemMeta(itemMeta)

        return itemStack
    }

    fun creditShopItem(player: Player, material: Material, productName: String, quantity: Int, buyPrice:Double, glowing: Boolean, lore: List<String>): ItemStack {
        val itemStack = ItemStack(material, quantity)
        val itemMeta = itemStack.itemMeta
        itemMeta.displayName(ModernText.miniModernText(productName, TagResolver.resolver(
            Placeholder.parsed("quantity", quantity.toString())
        )))
        if (glowing) {
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            itemMeta.addEnchant(Enchantment.ARROW_DAMAGE, 1, false)
        }
        if (lore.isNotEmpty()) {
            val newList: MutableList<Component> = ArrayList()
            for (loreItem in lore) {
                newList.add(ModernText.miniModernText(ModernText.papi(player, loreItem), TagResolver.resolver(
                    Placeholder.parsed("price", buyPrice.toString()),
                )))
            }
            itemMeta.lore(newList)
        }
        itemStack.setItemMeta(itemMeta)

        return itemStack
    }

    fun shopItem(material: Material, amount: Int): ItemStack {
        val itemStack = ItemStack(material, amount)

        return itemStack
    }

    fun shopItem(material: Material, name: String): ItemStack {
        val itemStack = ItemStack(material, 1)
        val itemMeta = itemStack.itemMeta
        itemMeta.displayName(ModernText.miniModernText(name))
        itemStack.setItemMeta(itemMeta)

        return itemStack
    }

    fun shopItem(material: Material, name: String, glowing: Boolean): ItemStack {
        val itemStack = ItemStack(material, 1)
        val itemMeta = itemStack.itemMeta
        itemMeta.displayName(ModernText.miniModernText(name))
        if (glowing) {
            itemMeta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)
            itemMeta.addEnchant(Enchantment.ARROW_DAMAGE, 1, false)
        }


        itemStack.setItemMeta(itemMeta)

        return itemStack
    }
}