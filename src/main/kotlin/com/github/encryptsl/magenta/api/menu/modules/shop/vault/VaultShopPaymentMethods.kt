package com.github.encryptsl.magenta.api.menu.modules.shop.vault

import com.github.encryptsl.kmono.lib.api.economy.components.EconomyDeposit
import com.github.encryptsl.kmono.lib.api.economy.components.EconomyWithdraw
import com.github.encryptsl.kmono.lib.api.economy.models.EconomyPaymentAction
import com.github.encryptsl.kmono.lib.api.economy.models.EconomyPaymentHolder
import com.github.encryptsl.kmono.lib.api.economy.models.EconomyPaymentImpl
import com.github.encryptsl.kmono.lib.extensions.hasPlayerRequiredItem
import com.github.encryptsl.kmono.lib.extensions.isPlayerInventoryFull
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.modules.shop.EconomyShopIntegration
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class VaultShopPaymentMethods(private val magenta: Magenta) : EconomyPaymentImpl {

    private val economyShopIntegration: EconomyShopIntegration by lazy { EconomyShopIntegration(magenta) }
    override fun buy(
        shopPaymentHolder: EconomyPaymentHolder,
        commands: MutableList<String>?,
        inventory: InventoryClickEvent
    ) {
        val player = inventory.whoClicked as Player
        val price = shopPaymentHolder.price

        if (!shopPaymentHolder.isOperationAllowed)
            return player.sendMessage(magenta.locale.translation("magenta.shop.error.buy.disabled"))

        if (isPlayerInventoryFull(player))
            return player.sendMessage(magenta.locale.translation("magenta.shop.error.inventory.full"))

        val transactions = EconomyWithdraw(player, price).transaction(magenta.vaultHook) ?: return

        economyShopIntegration.doVaultTransaction(player,
            EconomyPaymentAction.BUY, transactions, shopPaymentHolder,"magenta.shop.success.buy", commands)
    }
    override fun sell(
        shopPaymentHolder: EconomyPaymentHolder,
        inventory: InventoryClickEvent
    ) {
        val player = inventory.whoClicked as Player
        val item = shopPaymentHolder.itemStack
        val price = shopPaymentHolder.price

        if (!shopPaymentHolder.isOperationAllowed)
            return player.sendMessage(magenta.locale.translation("magenta.shop.error.sell.disabled"))

        if (!hasPlayerRequiredItem(player, item))
            return player.sendMessage(magenta.locale.translation("magenta.shop.error.empty.no.item"))

        val transactions = EconomyDeposit(player, price).transaction(magenta.vaultHook) ?: return

        economyShopIntegration.doVaultTransaction(player,
            EconomyPaymentAction.SELL, transactions, shopPaymentHolder,"magenta.shop.success.sell", null)
    }

}