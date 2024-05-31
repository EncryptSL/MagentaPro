package com.github.encryptsl.magenta.api.menu.modules.shop.vault

import com.github.encryptsl.kmono.lib.api.economy.components.EconomyDeposit
import com.github.encryptsl.kmono.lib.api.economy.components.EconomyUniversalBuilder
import com.github.encryptsl.kmono.lib.api.economy.components.EconomyWithdraw
import com.github.encryptsl.kmono.lib.api.economy.models.EconomyDataPayment
import com.github.encryptsl.kmono.lib.api.economy.models.EconomyPaymentAction
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.modules.shop.Product
import org.bukkit.entity.Player

class VaultShopPaymentMethods(private val magenta: Magenta)  {

    fun buy(
        player: Player,
        holder: EconomyDataPayment<Product>,
        isBuyAllowed: Boolean,
        commands: List<String>
    ) {
        val economyUniversalBuilder = EconomyUniversalBuilder(player)

        val withdraw = EconomyWithdraw(player, holder.product.productPrice).transaction(magenta.vaultHook) ?: return

        economyUniversalBuilder.setEconomyProvider(withdraw).setEconomyIntegration(EconomyVaultIntegration(
            magenta, "magenta.shop.success.buy", EconomyPaymentAction.BUY, holder, commands
        )).setLocale(magenta.locale).setOperation(isBuyAllowed).buy()
    }

    fun sell(
        player: Player,
        holder: EconomyDataPayment<Product>,
        isSellAllowed: Boolean,
        commands: List<String>
    ) {
        val economyUniversalBuilder = EconomyUniversalBuilder(player)

        val deposit = EconomyDeposit(player, holder.product.productPrice).transaction(magenta.vaultHook) ?: return

        economyUniversalBuilder.setEconomyProvider(deposit).setEconomyIntegration(EconomyVaultIntegration(
            magenta, "magenta.shop.success.sell", EconomyPaymentAction.SELL, holder, commands
        )).setLocale(magenta.locale).setOperation(isSellAllowed).setItemStack(holder.product.itemStack).sell()
    }


}