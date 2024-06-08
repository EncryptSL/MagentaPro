package com.github.encryptsl.magenta.api.menu.modules.shop.vault

import com.github.encryptsl.kmono.lib.api.economy.EconomyService
import com.github.encryptsl.kmono.lib.api.economy.EconomyTransactionResponse
import com.github.encryptsl.kmono.lib.api.economy.models.EconomyDataPayment
import com.github.encryptsl.kmono.lib.api.economy.models.EconomyPaymentAction
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.shop.ShopBuyEvent
import com.github.encryptsl.magenta.api.events.shop.ShopSellEvent
import com.github.encryptsl.magenta.api.menu.modules.shop.Product
import com.github.encryptsl.magenta.api.menu.modules.shop.helpers.ShopHelper
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.entity.Player

class EconomyVaultIntegration(
    private val magenta: Magenta,
    private val message: String,
    private val action: EconomyPaymentAction,
    private val holder: EconomyDataPayment<Product>,
    private val commands: List<String>
) : EconomyService {

    override fun doTransaction(player: Player, process: EconomyTransactionResponse) {
        if (process == EconomyTransactionResponse.ERROR_ENOUGH_BALANCE)
            return player.sendMessage(magenta.locale.translation("magenta.shop.error.not.enough.money"))

        if (process == EconomyTransactionResponse.SUCCESS) {
            val product = holder.product
            val itemStack = product.itemStack
            val price = product.productPrice

            player.sendMessage(magenta.locale.translation(message, TagResolver.resolver(
                Placeholder.component("item", product.displayName),
                Placeholder.parsed("quantity", product.amount.toString()),
                Placeholder.parsed("price", price.toString())
            )))

            when (action) {
                EconomyPaymentAction.SELL -> {
                    magenta.pluginManager.callEvent(ShopSellEvent(player, itemStack.type.name, price.toInt(), product.amount))
                    player.inventory.removeItem(product.itemStack)
                }
                EconomyPaymentAction.BUY -> {
                    magenta.pluginManager.callEvent(ShopBuyEvent(player, itemStack.type.name, price.toInt(), product.amount))
                    if (commands.isEmpty())
                        player.inventory.addItem(itemStack)
                    else
                        ShopHelper.giveRewards(commands, player.name, product.amount)
                }
            }
        }
    }
}