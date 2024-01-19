package com.github.encryptsl.magenta.api

import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ItemFactory {
    fun item(material: Material, itemName: String, lores: List<String>, glowing: Boolean): ItemStack {
        return ItemBuilder(material, 1)
            .setName(ModernText.miniModernText(itemName))
            .setGlowing(glowing)
            .addLore(lores.map { ModernText.miniModernText(it) }.toMutableList()).create()
    }

    fun shopItem(material: Material, buyPrice:Double, sellPrice:Double, fileConfiguration: FileConfiguration): ItemStack {
        val itemStack = ItemBuilder(material, 1)
        val lore = fileConfiguration.getStringList("shop.gui.item_lore")

        if (lore.isNotEmpty()) {
            val lores = lore.map {
                ModernText.miniModernText(it, TagResolver.resolver(
                    Placeholder.parsed("price", buyPrice.toString()),
                    Placeholder.parsed("sell", sellPrice.toString()),
                ))
            }.toMutableList()
            itemStack.addLore(lores)
        }
        if (fileConfiguration.contains("shop.items.${material.name}.options")) {
            if (fileConfiguration.contains("shop.items.${material.name}.options.color")) {
                val color: Color = Color.fromRGB(fileConfiguration.getInt("shop.items.${material.name}.options.color"))
                itemStack.setPotion(true, color)
            }
        }

        return itemStack.create()
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
        val itemBuilder = ItemBuilder(material, quantity)

        itemBuilder.setName(ModernText.miniModernText(productName, TagResolver.resolver(
            Placeholder.parsed("quantity", quantity.toString())
        )))

        if (lore.isNotEmpty()) {
            val lores = lore.map {
                ModernText.miniModernText(ModernText.papi(player, it), TagResolver.resolver(
                    Placeholder.parsed("price", buyPrice.toString()),
                ))
            }.toMutableList()
            itemBuilder.addLore(lores)
        }

        if (hasOptions) {
            itemBuilder.setGlowing(glowing)
            if (hasColor) {
                itemBuilder.setPotion(isPotion, Color.fromRGB(color))
            }
        }

        return itemBuilder.create()
    }

    fun shopItem(material: Material, name: String): ItemStack {
        return ItemBuilder(material, 1).setName(ModernText.miniModernText(name)).create()
    }

    fun shopItem(material: Material, amount: Int, name: String): ItemStack {
        return ItemBuilder(material, amount).setName(ModernText.miniModernText(name)).create()
    }

}