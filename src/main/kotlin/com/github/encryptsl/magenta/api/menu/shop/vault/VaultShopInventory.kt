package com.github.encryptsl.magenta.api.menu.shop.vault

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.shop.EconomyShopIntegration
import com.github.encryptsl.magenta.api.menu.shop.ShopAction
import com.github.encryptsl.magenta.api.menu.shop.ShopPaymentInformation
import com.github.encryptsl.magenta.api.menu.shop.TransactionType
import com.github.encryptsl.magenta.api.menu.shop.economy.components.EconomyDeposit
import com.github.encryptsl.magenta.api.menu.shop.economy.components.EconomyWithdraw
import com.github.encryptsl.magenta.api.menu.shop.helpers.ShopHelper
import com.github.encryptsl.magenta.common.hook.vault.VaultHook
import com.github.encryptsl.magenta.common.utils.ModernText
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class VaultShopInventory(private val magenta: Magenta) : ShopAction {

    private val economyShopIntegration: EconomyShopIntegration by lazy { EconomyShopIntegration(magenta) }
    override fun buy(
        shopPaymentInformation: ShopPaymentInformation,
        isCommand: Boolean,
        commands: MutableList<String>?,
        inventory: InventoryClickEvent
    ) {
        val player = inventory.whoClicked as Player
        val item = shopPaymentInformation.itemStack
        val price = shopPaymentInformation.price

        if (!shopPaymentInformation.isOperationAllowed)
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.shop.error.buy.disabled")))

        if (ShopHelper.isPlayerInventoryFull(player))
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.shop.error.inventory.full")))

        val fullPrice = ShopHelper.calcPrice(item.amount, price)

        val transactions = EconomyWithdraw(player, fullPrice).transaction(VaultHook(magenta)) ?: return

        economyShopIntegration.doVaultTransaction(player,
            TransactionType.BUY, transactions, "magenta.shop.success.buy", fullPrice, item, commands, isCommand)
    }
    override fun sell(
        shopPaymentInformation: ShopPaymentInformation,
        inventory: InventoryClickEvent
    ) {
        val player = inventory.whoClicked as Player
        val item = shopPaymentInformation.itemStack
        val price = shopPaymentInformation.price

        if (!shopPaymentInformation.isOperationAllowed)
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.shop.error.sell.disabled")))

        if (!player.inventory.contains(item.type))
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.shop.error.empty.no.item")))

        val fullPrice = ShopHelper.calcPrice(item.amount, price)
        val transactions = EconomyDeposit(player, fullPrice).transaction(VaultHook(magenta)) ?: return

        economyShopIntegration.doVaultTransaction(player,
            TransactionType.SELL, transactions, "magenta.shop.success.sell", fullPrice, item, null, true)
    }

}