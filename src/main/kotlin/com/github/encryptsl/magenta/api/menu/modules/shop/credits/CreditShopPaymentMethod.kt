package com.github.encryptsl.magenta.api.menu.modules.shop.credits

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.modules.shop.economy.components.EconomyWithdraw
import com.github.encryptsl.magenta.api.menu.modules.shop.economy.models.EconomyShopIntegration
import com.github.encryptsl.magenta.api.menu.modules.shop.helpers.ShopHelper
import com.github.encryptsl.magenta.common.hook.creditlite.CreditLiteHook
import net.kyori.adventure.text.Component
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player

class CreditShopPaymentMethod(private val magenta: Magenta) {

    private val economyShopIntegration = EconomyShopIntegration(magenta)

    fun buyItem(humanEntity: HumanEntity, config: FileConfiguration, item: String, displayName: Component, isBuyAllowed: Boolean) {
        val player = humanEntity as Player

        if (!isBuyAllowed)
            return player.sendMessage(magenta.locale.translation("magenta.shop.error.buy.disabled"))

        if (ShopHelper.isPlayerInventoryFull(player))
            return player.sendMessage(magenta.locale.translation("magenta.shop.error.inventory.full"))

        val price = config.getDouble("menu.items.$item.buy.price")
        val quantity = config.getInt("menu.items.$item.buy.quantity")
        val commands = config.getStringList("menu.items.$item.commands")

        val transactionErrors = EconomyWithdraw(player, price).transaction(CreditLiteHook(magenta)) ?: return

        economyShopIntegration.doCreditTransaction(player, transactionErrors, "magenta.shop.success.buy", displayName, price, quantity, commands)
    }


}