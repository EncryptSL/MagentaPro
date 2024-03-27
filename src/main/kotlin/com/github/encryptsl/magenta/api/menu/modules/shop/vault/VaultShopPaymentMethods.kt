package com.github.encryptsl.magenta.api.menu.modules.shop.vault

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.modules.shop.economy.components.EconomyDeposit
import com.github.encryptsl.magenta.api.menu.modules.shop.economy.components.EconomyWithdraw
import com.github.encryptsl.magenta.api.menu.modules.shop.economy.models.EconomyShopIntegration
import com.github.encryptsl.magenta.api.menu.modules.shop.economy.models.ShopPaymentAction
import com.github.encryptsl.magenta.api.menu.modules.shop.economy.models.ShopPaymentHolder
import com.github.encryptsl.magenta.api.menu.modules.shop.economy.models.ShopPaymentImpl
import com.github.encryptsl.magenta.api.menu.modules.shop.helpers.ShopHelper
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class VaultShopPaymentMethods(private val magenta: Magenta) : ShopPaymentImpl {

    private val economyShopIntegration: EconomyShopIntegration by lazy { EconomyShopIntegration(magenta) }
    override fun buy(
        shopPaymentHolder: ShopPaymentHolder,
        commands: MutableList<String>?,
        inventory: InventoryClickEvent
    ) {
        val player = inventory.whoClicked as Player
        val price = shopPaymentHolder.price

        if (!shopPaymentHolder.isOperationAllowed)
            return player.sendMessage(magenta.localeConfig.translation("magenta.shop.error.buy.disabled"))

        if (ShopHelper.isPlayerInventoryFull(player))
            return player.sendMessage(magenta.localeConfig.translation("magenta.shop.error.inventory.full"))

        val transactions = EconomyWithdraw(player, price).transaction(magenta.vaultHook) ?: return

        economyShopIntegration.doVaultTransaction(player,
            ShopPaymentAction.BUY, transactions, shopPaymentHolder,"magenta.shop.success.buy", commands)
    }
    override fun sell(
        shopPaymentHolder: ShopPaymentHolder,
        inventory: InventoryClickEvent
    ) {
        val player = inventory.whoClicked as Player
        val item = shopPaymentHolder.itemStack
        val price = shopPaymentHolder.price

        if (!shopPaymentHolder.isOperationAllowed)
            return player.sendMessage(magenta.localeConfig.translation("magenta.shop.error.sell.disabled"))

        if (!ShopHelper.hasPlayerRequiredItem(player, item))
            return player.sendMessage(magenta.localeConfig.translation("magenta.shop.error.empty.no.item"))

        val transactions = EconomyDeposit(player, price).transaction(magenta.vaultHook) ?: return

        economyShopIntegration.doVaultTransaction(player,
            ShopPaymentAction.SELL, transactions, shopPaymentHolder,"magenta.shop.success.sell", null)
    }

}