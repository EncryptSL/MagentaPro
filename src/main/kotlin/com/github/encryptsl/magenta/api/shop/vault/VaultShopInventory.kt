package com.github.encryptsl.magenta.api.shop.vault

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.shop.EconomyShopIntegration
import com.github.encryptsl.magenta.api.shop.ShopAction
import com.github.encryptsl.magenta.api.shop.ShopPaymentInformation
import com.github.encryptsl.magenta.api.shop.TransactionType
import com.github.encryptsl.magenta.api.shop.helpers.ShopHelper
import com.github.encryptsl.magenta.common.hook.vault.VaultHook
import com.github.encryptsl.magenta.common.utils.ModernText
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class VaultShopInventory(private val magenta: Magenta, private val vault: VaultHook) : ShopAction {

    private val economyShopIntegration: EconomyShopIntegration by lazy { EconomyShopIntegration(magenta) }
    override fun buy(
        shopPaymentInformation: ShopPaymentInformation,
        isItem: Boolean,
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

        economyShopIntegration.doVaultTransaction(player,
            TransactionType.BUY, vault.withdraw(player, fullPrice), "magenta.shop.success.buy", fullPrice, item, commands, isItem)
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

        economyShopIntegration.doVaultTransaction(player,
            TransactionType.SELL, vault.deposit(player, fullPrice), "magenta.shop.success.sell", fullPrice, item, null, true)
    }

}