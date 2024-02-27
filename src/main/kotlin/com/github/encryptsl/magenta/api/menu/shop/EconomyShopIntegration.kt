package com.github.encryptsl.magenta.api.menu.shop

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.shop.CreditShopBuyEvent
import com.github.encryptsl.magenta.api.events.shop.ShopBuyEvent
import com.github.encryptsl.magenta.api.events.shop.ShopSellEvent
import com.github.encryptsl.magenta.api.menu.shop.economy.TransactionErrors
import com.github.encryptsl.magenta.api.menu.shop.helpers.ShopHelper
import com.github.encryptsl.magenta.api.scheduler.SchedulerMagenta
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.entity.Player

class EconomyShopIntegration(private val magenta: Magenta) {
    fun doVaultTransaction(
        player: Player,
        transactionType: TransactionType,
        transactionErrors: TransactionErrors,
        shopPaymentInformation: ShopPaymentInformation,
        message: String,
        commands: MutableList<String>?,
        isCommand: Boolean?
    ) {
        if (transactionErrors == TransactionErrors.ERROR_ENOUGH_BALANCE)
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.shop.error.not.enough.money")))

        if (transactionErrors == TransactionErrors.SUCCESS) {
            val item = shopPaymentInformation.itemStack
            val price = shopPaymentInformation.price
            player.sendMessage(
                ModernText.miniModernText(
                    magenta.localeConfig.getMessage(message), TagResolver.resolver(
                        Placeholder.component("item", item.displayName()),
                        Placeholder.parsed("quantity", item.amount.toString()),
                        Placeholder.parsed("price", price.toString())
                    )
                )
            )

            when (transactionType) {
                TransactionType.SELL -> {
                    SchedulerMagenta.doSync(magenta) {
                        magenta.pluginManager.callEvent(ShopSellEvent(player, item.type.name, price.toInt(), item.amount))
                    }
                    player.inventory.removeItem(item)
                }
                TransactionType.BUY -> {
                    SchedulerMagenta.doSync(magenta) {
                        magenta.pluginManager.callEvent(ShopBuyEvent(player, item.type.name, price.toInt(), item.amount))
                    }
                    if (isCommand == false && commands.isNullOrEmpty())
                        player.inventory.addItem(item)
                    else
                        commands?.let { ShopHelper.giveRewards(it, player.name, item.amount) }
                }
            }
        }
    }

    fun doCreditTransaction(player: Player, transactionErrors: TransactionErrors, message: String, product: Component, price: Double, quantity: Int, commands: MutableList<String>) {
        if (transactionErrors == TransactionErrors.ERROR_ENOUGH_BALANCE)
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.shop.error.not.enough.credit")))

        if (transactionErrors == TransactionErrors.SUCCESS) {
            SchedulerMagenta.doSync(magenta) {
                magenta.pluginManager.callEvent(CreditShopBuyEvent(player, price.toInt(), quantity))
            }
            player.sendMessage(
                ModernText.miniModernText(
                    magenta.localeConfig.getMessage(message), TagResolver.resolver(
                        Placeholder.component("item", product),
                        Placeholder.parsed("quantity", quantity.toString()),
                        Placeholder.parsed("price", price.toString())
                    )
                )
            )
            ShopHelper.giveRewards(commands, player.name, quantity)
        }
    }


}