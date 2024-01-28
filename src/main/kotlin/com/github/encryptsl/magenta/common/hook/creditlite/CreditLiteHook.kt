package com.github.encryptsl.magenta.common.hook.creditlite

import com.github.encryptsl.credit.CreditLite
import com.github.encryptsl.magenta.Magenta
import org.bukkit.entity.Player

class CreditLiteHook(private val magenta: Magenta) {

    fun setupCreditLite(): Boolean {
        return try {
            Class.forName("com.github.encryptsl.credit.CreditLite")
            true
        } catch (e : ClassNotFoundException) {
            false
        }
    }

    fun hasCredits(player: Player, amount: Double): Boolean {
        if (!setupCreditLite())
            throw CreditException(magenta.localeConfig.getMessage("magenta.missing.credits.economy"))

        return CreditLite().getAPI().has(player, amount)
    }

    fun getCredits(player: Player): Double {
        if (!setupCreditLite())
            throw CreditException(magenta.localeConfig.getMessage("magenta.missing.credits.economy"))

        return CreditLite().getAPI().getBalance(player)
    }

    fun depositCredits(player: Player, amount: Double)
    {
        if (!setupCreditLite())
            throw CreditException(magenta.localeConfig.getMessage("magenta.missing.credits.economy"))

        CreditLite().getAPI().deposit(player, amount)
    }

    fun withdrawCredits(player: Player, amount: Double) {
        if (!setupCreditLite())
            throw CreditException(magenta.localeConfig.getMessage("magenta.missing.credits.economy"))

        CreditLite().getAPI().withdraw(player, amount)
    }

}