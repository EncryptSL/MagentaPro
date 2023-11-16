package com.github.encryptsl.magenta.api

import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta

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

    fun item(material: Material, itemName: String, lores: List<String>, glowing: Boolean): ItemStack {
        val itemStack = ItemStack(material, 1)
        val itemMeta = itemStack.itemMeta
        itemMeta.displayName(ModernText.miniModernText(itemName))

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

    fun shopItem(material: Material, buyPrice:Double, sellPrice:Double, fileConfiguration: FileConfiguration): ItemStack {
        val itemStack = shopItem(material, 1)
        var itemMeta = itemStack.itemMeta
        val lore = fileConfiguration.getStringList("shop.gui.item_lore")

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
        if (fileConfiguration.contains("shop.items.${material.name}.options")) {
            if (fileConfiguration.contains("shop.items.${material.name}.options.color")) {
                itemMeta = itemMeta as PotionMeta
                itemMeta.color = Color.fromRGB(fileConfiguration.getInt("shop.items.${material.name}.options.color"))
            }
        }

        itemStack.setItemMeta(itemMeta)

        return itemStack
    }
    fun creditShopItem(
        player: Player,
        material: Material,
        productName: String,
        quantity: Int,
        buyPrice:Double,
        glowing: Boolean,
        hasOptions: Boolean,
        isPotion: Boolean,
        hasColor: Boolean,
        color: Int,
        lore: List<String>
    ): ItemStack {
        val itemStack = shopItem(material, quantity)
        var itemMeta = itemStack.itemMeta
        itemMeta.displayName(ModernText.miniModernText(productName, TagResolver.resolver(
            Placeholder.parsed("quantity", quantity.toString())
        )))
        if (lore.isNotEmpty()) {
            val newList: MutableList<Component> = ArrayList()
            for (loreItem in lore) {
                newList.add(ModernText.miniModernText(ModernText.papi(player, loreItem), TagResolver.resolver(
                    Placeholder.parsed("price", buyPrice.toString()),
                )))
            }
            itemMeta.lore(newList)
        }

        if (hasOptions) {
            if (glowing) {
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
                itemMeta.addEnchant(Enchantment.ARROW_DAMAGE, 1, false)
            }
            if (isPotion) {
                if (hasColor) {
                    itemMeta = itemMeta as PotionMeta
                    itemMeta.color = Color.fromRGB(color)
                }
            }
        }

        itemStack.setItemMeta(itemMeta)

        return itemStack
    }

    fun shopItem(material: Material, amount: Int): ItemStack {
        return ItemStack(material, amount)
    }

    fun shopItem(material: Material, name: String): ItemStack {
        val itemStack = shopItem(material, 1)
        val itemMeta = itemStack.itemMeta
        itemMeta.displayName(ModernText.miniModernText(name))
        itemStack.setItemMeta(itemMeta)

        return itemStack
    }

    fun shopItem(material: Material, name: String, glowing: Boolean): ItemStack {
        val itemStack = shopItem(material, 1)
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