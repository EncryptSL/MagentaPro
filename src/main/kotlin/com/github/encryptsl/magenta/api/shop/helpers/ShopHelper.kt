package com.github.encryptsl.magenta.api.shop.helpers

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.ShopConfig
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

    fun giveRewards(commands: MutableList<String>, username: String) {
        commands.forEach { command ->
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", username).replace("%player%", username))
        }
    }

    @JvmStatic
    fun reloadShopConfigs(magenta: Magenta) {
        val shopConfig = magenta.shopConfig.getConfig()
        shopConfig.getConfigurationSection("shop.categories")?.getKeys(false)?.forEach { category ->
            val categoryConfig = ShopConfig(magenta, "shop/categories/vault/$category.yml")
            if (categoryConfig.fileExist()) {
                categoryConfig.reload()
                categoryConfig.save()
                magenta.logger.config("Shop config $category.yml was reloaded !")
            } else {
                magenta.logger.config("Shop config $category.yml not exist !")
            }
        }
        val zeusConfig = magenta.zeusShopConfig.getConfig()
        zeusConfig.getConfigurationSection("shop.categories")?.getKeys(false)?.forEach { category ->
            val categoryConfig = ShopConfig(magenta, "shop/categories/zeus/$category.yml")
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