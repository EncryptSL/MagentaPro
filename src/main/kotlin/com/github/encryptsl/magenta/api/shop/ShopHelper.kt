package com.github.encryptsl.magenta.api.shop

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.ShopConfig
import org.bukkit.entity.HumanEntity

object ShopHelper {
    @JvmStatic
    fun isPlayerInventoryFull(player: HumanEntity): Boolean {
        return player.inventory.firstEmpty() == -1
    }

    fun calcPrice(amount: Int, price: Double): Double {
        return (amount.times(price))
    }

    @JvmStatic
    fun reloadShopConfigs(magenta: Magenta) {
        val shopConfig = magenta.shopConfig.getConfig()
        shopConfig.getConfigurationSection("shop.categories")?.getKeys(false)?.forEach { category ->
            val categoryConfig = ShopConfig(magenta, "shop/categories/$category.yml")
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