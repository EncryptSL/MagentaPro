package com.github.encryptsl.magenta.api

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.kmono.lib.utils.ItemCreator
import com.github.encryptsl.magenta.Magenta
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.math.BigDecimal

class ItemFactory {

    fun shopItem(
        itemName: String,
        material: Material,
        buyPrice: BigDecimal,
        sellPrice: BigDecimal,
        isBuyAllowed: Boolean,
        isSellAllowed: Boolean,
        config: FileConfiguration
    ): ItemStack {
        val itemStack = ItemCreator(material, 1).setName(ModernText.miniModernText(itemName))
        val lore = config.getStringList("menu.gui.item_lore")

        if (lore.isNotEmpty()) {
            val lores = lore.map {
                ModernText.miniModernText(it, TagResolver.resolver(
                    Placeholder.parsed("price", buyPrice.toString()),
                    Placeholder.parsed("sell", sellPrice.toString()),
                ))
            }.toMutableList()

            if (!isBuyAllowed) {
                lores.removeAt(0)
            }

            if (!isSellAllowed) {
                lores.removeAt(1)
            }

            itemStack.addLore(lores)
        }
        if (config.contains("menu.items.${itemName}.options")) {
            if (config.contains("menu.items.${itemName}.options.color")) {
                val color: Color = Color.fromRGB(config.getInt("menu.items.${itemName}.options.color"))
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
        val itemBuilder = ItemCreator(material, quantity)

        itemBuilder.setName(ModernText.miniModernText(productName, TagResolver.resolver(
            Placeholder.parsed("quantity", quantity.toString())
        )))

        if (lore.isNotEmpty()) {
            val lores = lore.map {
                ModernText.miniModernText(player, it, TagResolver.resolver(
                    Placeholder.parsed("price", buyPrice.toString()),
                    Placeholder.parsed("credits", Magenta.instance.vaultUnlockedHook.getBalance(player, "credits").toPlainString())
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
}