package com.github.encryptsl.magenta.api.menu.shop.credits

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.shop.EconomyShopIntegration
import com.github.encryptsl.magenta.api.menu.shop.economy.components.EconomyWithdraw
import com.github.encryptsl.magenta.api.menu.shop.helpers.ShopHelper
import com.github.encryptsl.magenta.common.hook.creditlite.CreditLiteHook
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class CreditShopInventory(private val magenta: Magenta) {

    private val economyShopIntegration = EconomyShopIntegration(magenta)

    fun buyItem(inventory: InventoryClickEvent, product: Component, price: Double, quantity: Int, commands: MutableList<String>, message: String, isBuyAllowed: Boolean) {
        val player = inventory.whoClicked as Player

        if (!isBuyAllowed)
            return player.sendMessage(magenta.localeConfig.translation("magenta.shop.error.buy.disabled"))

        if (ShopHelper.isPlayerInventoryFull(player))
            return player.sendMessage(magenta.localeConfig.translation("magenta.shop.error.inventory.full"))

        val transactionErrors = EconomyWithdraw(player, price).transaction(CreditLiteHook(magenta)) ?: return

        economyShopIntegration.doCreditTransaction(player, transactionErrors, message, product, price, quantity, commands)
    }


}