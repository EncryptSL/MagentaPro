package com.github.encryptsl.magenta.common.model

import com.github.encryptsl.magenta.Magenta
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration

class ShopManager(private val magenta: Magenta) {

    fun getShopProducts(config: FileConfiguration): Set<ShopProduct> {
        val menuItems = config.getConfigurationSection("menu.items")?.getKeys(false) ?: return emptySet()
        val products = mutableSetOf<ShopProduct>()

        for (product in menuItems) {
            if (!config.contains("menu.items.${product}")) continue
            val material = Material.getMaterial(
                config.getString("menu.items.${product}.icon").toString()
            ) ?: continue

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

    data class ShopProduct(
        val name: String?,
        val material: Material,
        val buyPrice: Double,
        val sellPrice: Double,
        val isBuyAllowed: Boolean = true,
        val isSellAllowed: Boolean = false,
        val commands: List<String>
    )
}