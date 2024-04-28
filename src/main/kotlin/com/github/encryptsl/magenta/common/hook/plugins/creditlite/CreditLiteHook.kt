package com.github.encryptsl.magenta.common.hook.creditlite

import com.github.encryptsl.credit.api.economy.CreditEconomy
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.modules.shop.economy.Economy
import com.github.encryptsl.magenta.api.menu.modules.shop.economy.MissingEconomyException
import com.github.encryptsl.magenta.common.hook.model.PluginHook
import org.bukkit.OfflinePlayer

class CreditLiteHook(private val magenta: Magenta) : PluginHook("CreditLite"), Economy {

    override fun hasBalance(player: OfflinePlayer, value: Double): Boolean {
        if (!isPluginEnabled())
            throw MissingEconomyException(magenta.localeConfig.getMessage("magenta.missing.credits.economy"))

        return CreditEconomy.has(player, value)
    }

    override fun deposit(player: OfflinePlayer, value: Double) {
        if (!isPluginEnabled())
            throw MissingEconomyException(magenta.localeConfig.getMessage("magenta.missing.credits.economy"))

        CreditEconomy.deposit(player, value)
    }

    override fun withdraw(player: OfflinePlayer, value: Double) {
        if (!isPluginEnabled())
            throw MissingEconomyException(magenta.localeConfig.getMessage("magenta.missing.credits.economy"))

        CreditEconomy.withdraw(player, value)
    }

    override fun getBalance(player: OfflinePlayer): Double {
        if (!isPluginEnabled()) return Double.MIN_VALUE

        return CreditEconomy.getBalance(player)
    }
}