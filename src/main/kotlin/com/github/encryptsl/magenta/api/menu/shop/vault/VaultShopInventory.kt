package com.github.encryptsl.magenta.api.menu.shop.vault

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.shop.EconomyShopIntegration
import com.github.encryptsl.magenta.api.menu.shop.ShopAction
import com.github.encryptsl.magenta.api.menu.shop.ShopPaymentInformation
import com.github.encryptsl.magenta.api.menu.shop.TransactionType
import com.github.encryptsl.magenta.api.menu.shop.economy.components.EconomyDeposit
import com.github.encryptsl.magenta.api.menu.shop.economy.components.EconomyWithdraw
import com.github.encryptsl.magenta.api.menu.shop.helpers.ShopHelper
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class VaultShopInventory(private val magenta: Magenta) : ShopAction {

    private val economyShopIntegration: EconomyShopIntegration by lazy { EconomyShopIntegration(magenta) }
    override fun buy(
        shopPaymentInformation: ShopPaymentInformation,
        commands: MutableList<String>?,
        inventory: InventoryClickEvent
    ) {
        val player = inventory.whoClicked as Player
        val price = shopPaymentInformation.price

        if (!shopPaymentInformation.isOperationAllowed)
            return player.sendMessage(magenta.localeConfig.translation("magenta.shop.error.buy.disabled"))

        if (ShopHelper.isPlayerInventoryFull(player))
            return player.sendMessage(magenta.localeConfig.translation("magenta.shop.error.inventory.full"))

        val transactions = EconomyWithdraw(player, price).transaction(magenta.vaultHook) ?: return

        economyShopIntegration.doVaultTransaction(player,
            TransactionType.BUY, transactions, shopPaymentInformation,"magenta.shop.success.buy", commands)
    }
    override fun sell(
        shopPaymentInformation: ShopPaymentInformation,
        inventory: InventoryClickEvent
    ) {
        val player = inventory.whoClicked as Player
        val item = shopPaymentInformation.itemStack
        val price = shopPaymentInformation.price

        if (!shopPaymentInformation.isOperationAllowed)
            return player.sendMessage(magenta.localeConfig.translation("magenta.shop.error.sell.disabled"))

        if (!ShopHelper.hasPlayerRequiredItem(player, item))
            return player.sendMessage(magenta.localeConfig.translation("magenta.shop.error.empty.no.item"))

        val transactions = EconomyDeposit(player, price).transaction(magenta.vaultHook) ?: return

        economyShopIntegration.doVaultTransaction(player,
            TransactionType.SELL, transactions, shopPaymentInformation,"magenta.shop.success.sell", null)
    }

}