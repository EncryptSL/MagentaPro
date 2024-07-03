package com.github.encryptsl.magenta.common.model

import com.github.encryptsl.magenta.Magenta
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration

class ShopManager(private val magenta: Magenta) {

    data class ShopProduct(
        val name: String?,
        val material: Material,
        val buyPrice: Double,
        val sellPrice: Double,
        val isBuyAllowed: Boolean = true,
        val isSellAllowed: Boolean = false,
        val commands: List<String>
    )

    fun getShopProducts(config: FileConfiguration): Set<ShopProduct> {
        val menuItems = config.getConfigurationSection("menu.items")?.getKeys(false) ?: return emptySet()
        val products = mutableSetOf<ShopProduct>()
        val iterator = menuItems.iterator()

        while (iterator.hasNext()) {
            val product = iterator.next()
            if (!config.contains("menu.items.${product}")) continue
            val material = Material.entries.firstOrNull {
                    el -> el.name.equals(config.getString("menu.items.${product}.icon").toString(), true)
            } ?: continue

            val itemName = config.getString("menu.items.${product}.name")
            val buyPrice = config.getDouble("menu.items.${product}.buy.price")
            val sellPrice = config.getDouble("menu.items.${product}.sell.price")

            val isBuyAllowed = config.contains("menu.items.${product}.buy.price")
            val isSellAllowed = config.contains("menu.items.${product}.sell.price")
            val commands = config.getStringList("menu.items.${product}.buy.commands")

            products.add(
                ShopProduct(
                    itemName,
                    material,
                    buyPrice,
                    sellPrice,
                    isBuyAllowed,
                    isSellAllowed,
                    commands
                )
            )
        }

        return products
    }

    fun getShopCategories(): Set<String> {
        return magenta
            .shopConfig
            .getConfig()
            .getConfigurationSection("menu.categories")
            ?.getKeys(false) ?: emptySet()
    }
}