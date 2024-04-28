package com.github.encryptsl.magenta.api.menu.modules.shop.helpers

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.config.UniversalConfig
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.Bukkit
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
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
    fun playSound(player: Player, type: String, volume: Float, pitch: Float) {
        player.playSound(Sound.sound().type(Key.key(type)).volume(volume).pitch(pitch).build())
    }

    @JvmStatic
    fun playSound(player: HumanEntity, type: String, volume: Float, pitch: Float) {
        player.playSound(Sound.sound().type(Key.key(type)).volume(volume).pitch(pitch).build())
    }

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
                val categoryConfig = UniversalConfig(magenta, "menu/shop/categories/$category.yml")
                if (categoryConfig.fileExist()) {
                    categoryConfig.reload()
                    categoryConfig.save()
                }
            }
            magenta.logger.info("VaultShop was reloaded with ${shopCategories.size} configs !")
        }
        
        val creditConfig = magenta.creditShopConfig.getConfig()
        val creditShopCategories = creditConfig.getConfigurationSection("menu.categories")?.getKeys(false)
        if (creditShopCategories != null) {
            for (category in creditShopCategories) {
                val categoryConfig = UniversalConfig(magenta, "menu/creditshop/categories/$category.yml")
                if (categoryConfig.fileExist()) {
                    categoryConfig.reload()
                    categoryConfig.save()
                }
            }
            magenta.logger.info("CreditShop was reloaded with ${creditShopCategories.size} configs !")
        }
    }
}