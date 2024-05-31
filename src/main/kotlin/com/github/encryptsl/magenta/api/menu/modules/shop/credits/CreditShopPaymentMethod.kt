package com.github.encryptsl.magenta.api.menu.modules.shop.credits

import com.github.encryptsl.kmono.lib.api.economy.components.EconomyUniversalBuilder
import com.github.encryptsl.kmono.lib.api.economy.components.EconomyWithdraw
import com.github.encryptsl.kmono.lib.api.economy.models.EconomyDataPayment
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.modules.shop.Product
import com.github.encryptsl.magenta.common.hook.creditlite.CreditLiteHook
import net.kyori.adventure.text.Component
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class CreditShopPaymentMethod(private val magenta: Magenta) {

    fun buy(
        player: Player,
        itemStack: ItemStack,
        displayName: Component,
        item: String,
        config: FileConfiguration,
        isBuyAllowed: Boolean
    ) {
        val price = config.getDouble("menu.items.$item.buy.price")
        val quantity = config.getInt("menu.items.$item.buy.quantity")
        val commands = config.getStringList("menu.items.$item.commands")

        val product: EconomyDataPayment<Product> = EconomyDataPayment(Product(itemStack, displayName, price, quantity))

        val withdraw = EconomyWithdraw(player, product.product.productPrice).transaction(CreditLiteHook(magenta)) ?: return

        val economyUniversalBuilder = EconomyUniversalBuilder(player)
        economyUniversalBuilder.setEconomyProvider(withdraw).setEconomyIntegration(EconomyCreditIntegration(
            magenta, "magenta.shop.success.buy", product, commands
        )).setOperation(isBuyAllowed).isCreditPayment(true).setItemStack(product.product.itemStack).buy()
    }


}