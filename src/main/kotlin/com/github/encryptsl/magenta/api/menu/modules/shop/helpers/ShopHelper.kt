package com.github.encryptsl.magenta.api.menu.modules.shop.helpers

import com.github.encryptsl.kmono.lib.api.config.UniversalConfig
import com.github.encryptsl.magenta.Magenta
import org.bukkit.Bukkit

object ShopHelper {

    @JvmStatic
    fun calcPrice(amount: Int, price: Double) = (amount.times(price))
    @JvmStatic
    fun giveRewards(commands: MutableList<String>, username: String, quantity: Int) {
        commands.forEach { command ->
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command
                .replace("{player}", username)
                .replace("%player%", username)
                .replace("%quantity%", quantity.toString())
                .replace("{quantity}", quantity.toString())
            )
        }
    }

    @JvmStatic
    fun reloadShopConfigs(magenta: Magenta) {
        val shopConfig = magenta.shopConfig.getConfig()
        val shopCategories = shopConfig.getConfigurationSection("menu.categories")?.getKeys(false)

        if (shopCategories != null) {
            for (category in shopCategories) {
                val categoryConfig = UniversalConfig("${magenta.dataFolder}/menu/shop/categories/$category.yml")
                if (!categoryConfig.exists()) continue

                categoryConfig.reload()
                categoryConfig.save()
            }
            magenta.logger.info("VaultShop are reloaded with ${shopCategories.size} configs !")
        }
        
        val creditConfig = magenta.creditShopConfig.getConfig()
        val creditShopCategories = creditConfig.getConfigurationSection("menu.categories")?.getKeys(false)
        if (creditShopCategories != null) {
            for (category in creditShopCategories) {
                val categoryConfig = UniversalConfig("${magenta.dataFolder}/menu/creditshop/categories/$category.yml")
                if (!categoryConfig.exists()) continue

                categoryConfig.reload()
                categoryConfig.save()
            }
            magenta.logger.info("CreditShop are reloaded with ${creditShopCategories.size} configs !")
        }
    }
}