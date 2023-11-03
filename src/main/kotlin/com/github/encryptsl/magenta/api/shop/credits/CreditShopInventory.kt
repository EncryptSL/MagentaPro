package com.github.encryptsl.magenta.api.shop.credits

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.shop.EconomyShopIntegration
import com.github.encryptsl.magenta.api.shop.helpers.ShopHelper
import com.github.encryptsl.magenta.common.hook.creditlite.CreditLiteHook
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class CreditShopInventory(private val magenta: Magenta, private val creditLiteHook: CreditLiteHook) {

    private val economyShopIntegration = EconomyShopIntegration(magenta)

    fun buyItem(inventory: InventoryClickEvent, product: Component, price: Double, quantity: Int, commands: MutableList<String>, message: String, isBuyAllowed: Boolean) {
        val player = inventory.whoClicked as Player

        if (!isBuyAllowed)
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.shop.error.buy.disabled")))

        if (ShopHelper.isPlayerInventoryFull(player))
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.shop.error.inventory.full")))

        economyShopIntegration.doCreditTransaction(player, creditLiteHook, message, product, price, quantity, commands)

    }


}