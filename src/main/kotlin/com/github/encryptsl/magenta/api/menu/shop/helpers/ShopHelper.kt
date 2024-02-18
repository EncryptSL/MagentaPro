package com.github.encryptsl.magenta.api.menu.shop.helpers

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.config.UniversalConfig
import org.bukkit.Bukkit
import org.bukkit.entity.HumanEntity

object ShopHelper {
    @JvmStatic
    fun isPlayerInventoryFull(player: HumanEntity): Boolean {
        return player.inventory.firstEmpty() == -1
    }

    fun calcPrice(amount: Int, price: Double): Double {
        return (amount.times(price))
    }

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
        for (category in shopConfig.getConfigurationSection("shop.categories")?.getKeys(false)!!) {
            val categoryConfig = UniversalConfig(magenta, "shop/categories/$category.yml")
            if (categoryConfig.fileExist()) {
                categoryConfig.reload()
                categoryConfig.save()
                magenta.logger.config("Shop config $category.yml was reloaded !")
            } else {
                magenta.logger.config("Shop config $category.yml not exist !")
            }
        }
        
        val creditConfig = magenta.creditShopConfig.getConfig()
        for (category in creditConfig.getConfigurationSection("shop.categories")?.getKeys(false)!!) {
            val categoryConfig = UniversalConfig(magenta, "creditshop/categories/$category.yml")
            if (categoryConfig.fileExist()) {
                categoryConfig.reload()
                categoryConfig.save()
                magenta.logger.config("Shop config $category.yml was reloaded !")
            } else {
                magenta.logger.config("Shop config $category.yml not exist !")
            }
        }
    }
}