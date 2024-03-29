package com.github.encryptsl.magenta.api.menu.modules.shop.economy.models

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.shop.CreditShopBuyEvent
import com.github.encryptsl.magenta.api.events.shop.ShopBuyEvent
import com.github.encryptsl.magenta.api.events.shop.ShopSellEvent
import com.github.encryptsl.magenta.api.menu.modules.shop.economy.TransactionProcess
import com.github.encryptsl.magenta.api.menu.modules.shop.helpers.ShopHelper
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.entity.Player

class EconomyShopIntegration(private val magenta: Magenta) {
    fun doVaultTransaction(
        player: Player,
        shopPaymentAction: ShopPaymentAction,
        transactionProcess: TransactionProcess,
        shopPaymentHolder: ShopPaymentHolder,
        message: String,
        commands: MutableList<String>?,
    ) {
        if (transactionProcess == TransactionProcess.ERROR_ENOUGH_BALANCE)
            return player.sendMessage(magenta.localeConfig.translation("magenta.shop.error.not.enough.money"))

        if (transactionProcess == TransactionProcess.SUCCESS) {
            val item = shopPaymentHolder.itemStack
            val price = shopPaymentHolder.price
            player.sendMessage(magenta.localeConfig.translation(message, TagResolver.resolver(
                Placeholder.component("item", item.displayName()),
                Placeholder.parsed("quantity", item.amount.toString()),
                Placeholder.parsed("price", price.toString())
            )))

            when (shopPaymentAction) {
                ShopPaymentAction.SELL -> {
                    magenta.pluginManager.callEvent(ShopSellEvent(player, item.type.name, price.toInt(), item.amount))
                    player.inventory.removeItem(item)
                }
                ShopPaymentAction.BUY -> {
                    magenta.pluginManager.callEvent(ShopBuyEvent(player, item.type.name, price.toInt(), item.amount))
                    if (commands.isNullOrEmpty())
                        player.inventory.addItem(item)
                    else
                        commands.let { ShopHelper.giveRewards(it, player.name, item.amount) }
                }
            }
        }
    }

    fun doCreditTransaction(player: Player, transactionProcess: TransactionProcess, message: String, product: Component, price: Double, quantity: Int, commands: MutableList<String>) {
        if (transactionProcess == TransactionProcess.ERROR_ENOUGH_BALANCE)
            return player.sendMessage(magenta.localeConfig.translation("magenta.shop.error.not.enough.credit"))

        if (transactionProcess == TransactionProcess.SUCCESS) {
            magenta.pluginManager.callEvent(CreditShopBuyEvent(player, price.toInt(), quantity))
            player.sendMessage(magenta.localeConfig.translation(message, TagResolver.resolver(
                Placeholder.component("item", product),
                Placeholder.parsed("quantity", quantity.toString()),
                Placeholder.parsed("price", price.toString())
            )))
            ShopHelper.giveRewards(commands, player.name, quantity)
        }
    }
}