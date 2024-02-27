package com.github.encryptsl.magenta.api.menu.shop.helpers

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.config.UniversalConfig
import org.bukkit.Bukkit
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.ItemStack

object ShopHelper {
    @JvmStatic
    fun isPlayerInventoryFull(player: HumanEntity): Boolean {
        return player.inventory.firstEmpty() == -1
    }

    @JvmStatic
    fun hasPlayerRequiredItem(player: HumanEntity, item: ItemStack): Boolean {
        return player.inventory.contains(item.type)
    }

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
        for (category in shopConfig.getConfigurationSection("menu.categories")?.getKeys(false)!!) {
            val categoryConfig = UniversalConfig(magenta, "menu/shop/categories/$category.yml")
            if (categoryConfig.fileExist()) {
                categoryConfig.reload()
                categoryConfig.save()
                magenta.logger.config("Shop config $category.yml was reloaded !")
            } else {
                magenta.logger.config("Shop config $category.yml not exist !")
            }
        }
        
        val creditConfig = magenta.creditShopConfig.getConfig()
        for (category in creditConfig.getConfigurationSection("menu.categories")?.getKeys(false)!!) {
            val categoryConfig = UniversalConfig(magenta, "menu/creditshop/categories/$category.yml")
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