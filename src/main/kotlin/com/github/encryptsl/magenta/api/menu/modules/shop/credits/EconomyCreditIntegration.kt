package com.github.encryptsl.magenta.api.menu.modules.shop.credits

import com.github.encryptsl.kmono.lib.api.economy.EconomyService
import com.github.encryptsl.kmono.lib.api.economy.EconomyTransactionResponse
import com.github.encryptsl.kmono.lib.api.economy.models.EconomyDataPayment
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.shop.CreditShopBuyEvent
import com.github.encryptsl.magenta.api.menu.modules.shop.Product
import com.github.encryptsl.magenta.api.menu.modules.shop.helpers.ShopHelper
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.entity.Player

class EconomyCreditIntegration(
    private val magenta: Magenta,
    private val message: String,
    private val holder: EconomyDataPayment<Product>,
    private val commands: List<String>
) : EconomyService {
    override fun doTransaction(player: Player, process: EconomyTransactionResponse) {
        when(process) {
            EconomyTransactionResponse.SUCCESS -> {
                val product = holder.product
                val price = product.productPrice

                magenta.pluginManager.callEvent(CreditShopBuyEvent(player, price.toInt(), product.amount))
                player.sendMessage(magenta.locale.translation(message, TagResolver.resolver(
                    Placeholder.component("item", product.displayName),
                    Placeholder.parsed("quantity", product.amount.toString()),
                    Placeholder.parsed("price", price.toString())
                )))
                ShopHelper.giveRewards(commands, player.name, product.amount)
            }
            EconomyTransactionResponse.ERROR_ENOUGH_BALANCE -> {
                player.sendMessage(magenta.locale.translation("magenta.shop.error.not.enough.credit"))
            }
        }
    }
}