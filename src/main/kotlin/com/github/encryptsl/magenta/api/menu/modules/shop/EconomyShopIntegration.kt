package com.github.encryptsl.magenta.api.menu.modules.shop

import com.github.encryptsl.kmono.lib.api.economy.EconomyTransactionProcess
import com.github.encryptsl.kmono.lib.api.economy.models.EconomyPaymentAction
import com.github.encryptsl.kmono.lib.api.economy.models.EconomyPaymentHolder
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.shop.CreditShopBuyEvent
import com.github.encryptsl.magenta.api.events.shop.ShopBuyEvent
import com.github.encryptsl.magenta.api.events.shop.ShopSellEvent
import com.github.encryptsl.magenta.api.menu.modules.shop.helpers.ShopHelper
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.entity.Player
import kotlin.collections.isNullOrEmpty
import kotlin.let

class EconomyShopIntegration(private val magenta: Magenta) {
    fun doVaultTransaction(
        player: Player,
        shopPaymentAction: EconomyPaymentAction,
        transactionProcess: EconomyTransactionProcess,
        shopPaymentHolder: EconomyPaymentHolder,
        message: String,
        commands: MutableList<String>?,
    ) {
        if (transactionProcess == EconomyTransactionProcess.ERROR_ENOUGH_BALANCE)
            return player.sendMessage(magenta.locale.translation("magenta.shop.error.not.enough.money"))

        if (transactionProcess == EconomyTransactionProcess.SUCCESS) {
            val item = shopPaymentHolder.itemStack
            val price = shopPaymentHolder.price
            player.sendMessage(magenta.locale.translation(message, TagResolver.resolver(
                Placeholder.component("item", item.displayName()),
                Placeholder.parsed("quantity", item.amount.toString()),
                Placeholder.parsed("price", price.toString())
            )))

            when (shopPaymentAction) {
                EconomyPaymentAction.SELL -> {
                    magenta.pluginManager.callEvent(ShopSellEvent(player, item.type.name, price.toInt(), item.amount))
                    player.inventory.removeItem(item)
                }
                EconomyPaymentAction.BUY -> {
                    magenta.pluginManager.callEvent(ShopBuyEvent(player, item.type.name, price.toInt(), item.amount))
                    if (commands.isNullOrEmpty())
                        player.inventory.addItem(item)
                    else
                        commands.let { ShopHelper.giveRewards(it, player.name, item.amount) }
                }
            }
        }
    }

    fun doCreditTransaction(player: Player, transactionProcess: EconomyTransactionProcess, message: String, product: Component, price: Double, quantity: Int, commands: MutableList<String>) {
        if (transactionProcess == EconomyTransactionProcess.ERROR_ENOUGH_BALANCE)
            return player.sendMessage(magenta.locale.translation("magenta.shop.error.not.enough.credit"))

        if (transactionProcess == EconomyTransactionProcess.SUCCESS) {
            magenta.pluginManager.callEvent(CreditShopBuyEvent(player, price.toInt(), quantity))
            player.sendMessage(magenta.locale.translation(message, TagResolver.resolver(
                Placeholder.component("item", product),
                Placeholder.parsed("quantity", quantity.toString()),
                Placeholder.parsed("price", price.toString())
            )))
            ShopHelper.giveRewards(commands, player.name, quantity)
        }
    }
}