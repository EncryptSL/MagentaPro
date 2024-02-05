package com.github.encryptsl.magenta.api.shop

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.shop.CreditShopBuyEvent
import com.github.encryptsl.magenta.api.events.shop.ShopBuyEvent
import com.github.encryptsl.magenta.api.events.shop.ShopSellEvent
import com.github.encryptsl.magenta.api.scheduler.SchedulerMagenta
import com.github.encryptsl.magenta.api.shop.helpers.ShopHelper
import com.github.encryptsl.magenta.common.hook.creditlite.CreditException
import com.github.encryptsl.magenta.common.hook.creditlite.CreditLiteHook
import com.github.encryptsl.magenta.common.hook.vault.VaultException
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class EconomyShopIntegration(private val magenta: Magenta) {
    fun doVaultTransaction(
        player: Player,
        transactionType: TransactionType,
        economyResponse: EconomyResponse,
        message: String,
        price: Double,
        item: ItemStack,
        commands: MutableList<String>?,
        isItem: Boolean?
    ) {
        try {
            if (economyResponse.transactionSuccess()) {
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
                        if (isItem == true && commands.isNullOrEmpty())
                            player.inventory.addItem(item)
                        else
                            commands?.let { ShopHelper.giveRewards(it, player.name, item.amount) }
                    }
                }
                player.updateInventory()
            } else {
                player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.shop.error.not.enough.money")))
            }
        } catch (e: VaultException) {
            player.sendMessage(ModernText.miniModernText(e.message ?: e.localizedMessage))
        }
    }

    fun doCreditTransaction(player: Player, creditLiteHook: CreditLiteHook, message: String, product: Component, price: Double, quantity: Int, commands: MutableList<String>) {
        try {
            if (!creditLiteHook.hasCredits(player, price))
                return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.shop.error.not.enough.credit")))

                SchedulerMagenta.doSync(magenta) {
                    magenta.pluginManager.callEvent(CreditShopBuyEvent(player, price.toInt(), quantity))
                }

                creditLiteHook.withdrawCredits(player, price)
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
        } catch (e : CreditException) {
            player.sendMessage(ModernText.miniModernText(e.message ?: e.localizedMessage))
        }
    }


}