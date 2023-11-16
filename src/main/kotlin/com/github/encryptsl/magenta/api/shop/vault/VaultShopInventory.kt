package com.github.encryptsl.magenta.api.shop.vault

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.shop.EconomyShopIntegration
import com.github.encryptsl.magenta.api.shop.TransactionType
import com.github.encryptsl.magenta.api.shop.helpers.ShopHelper
import com.github.encryptsl.magenta.common.hook.vault.VaultHook
import com.github.encryptsl.magenta.common.utils.ModernText
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class VaultShopInventory(private val magenta: Magenta, private val vault: VaultHook) {

    private val economyShopIntegration = EconomyShopIntegration(magenta)


    fun buyItem(item: ItemStack, isBuyAllowed: Boolean, price: Double, inventory: InventoryClickEvent) {
        val player = inventory.whoClicked as Player
        if (!isBuyAllowed)
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.shop.error.buy.disabled")))

        if (ShopHelper.isPlayerInventoryFull(player))
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.shop.error.inventory.full")))

        val fullPrice = ShopHelper.calcPrice(item.amount, price)

        economyShopIntegration.doVaultTransaction(player,
            TransactionType.BUY, vault.withdraw(player, fullPrice), "magenta.shop.success.buy", fullPrice, item)
    }

    fun sellItem(item: ItemStack, isSellAllowed: Boolean, price: Double, inventory: InventoryClickEvent) {
        val player = inventory.whoClicked as Player
        if (!isSellAllowed)
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.shop.error.sell.disabled")))

        if (!player.inventory.contains(item.type))
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.shop.error.empty.no.item")))

        val fullPrice = ShopHelper.calcPrice(item.amount, price)

        economyShopIntegration.doVaultTransaction(player,
            TransactionType.SELL, vault.deposit(player, fullPrice), "magenta.shop.success.sell", fullPrice, item)
    }

}