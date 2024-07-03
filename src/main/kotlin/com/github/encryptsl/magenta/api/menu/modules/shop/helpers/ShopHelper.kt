package com.github.encryptsl.magenta.api.menu.modules.shop.helpers

import com.github.encryptsl.kmono.lib.api.config.UniversalConfig
import com.github.encryptsl.magenta.Magenta
import org.bukkit.Bukkit

object ShopHelper {
    @JvmStatic
    fun giveRewards(commands: List<String>, username: String, quantity: Int) {
        val iterator = commands.iterator()
        while (iterator.hasNext()) {
            val command = iterator.next()
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
        shopConfig.getConfigurationSection("menu.categories")?.getKeys(false)?.let {
            for (category in it) {
                val categoryConfig = UniversalConfig("${magenta.dataFolder}/menu/shop/categories/$category.yml")
                if (!categoryConfig.exists()) continue

                categoryConfig.reload()
                categoryConfig.save()
            }
            magenta.logger.info("VaultShop are reloaded with ${it.size} configs !")
        }

        val creditConfig = magenta.creditShopConfig.getConfig()
        creditConfig.getConfigurationSection("menu.categories")?.getKeys(false)?.let {
            for (category in it) {
                val categoryConfig = UniversalConfig("${magenta.dataFolder}/menu/creditshop/categories/$category.yml")
                if (!categoryConfig.exists()) continue

                categoryConfig.reload()
                categoryConfig.save()
            }
            magenta.logger.info("CreditShop are reloaded with ${it.size} configs !")
        }
    }
}