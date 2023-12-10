package com.github.encryptsl.magenta.common.hook.creditlite

import com.github.encryptsl.credit.api.interfaces.CreditAPI
import com.github.encryptsl.magenta.Magenta
import org.bukkit.entity.Player
import org.bukkit.plugin.ServicesManager

class CreditLiteHook(private val magenta: Magenta) {

    private var eco: CreditAPI? = null

    fun setupCreditLite(): Boolean {
        if (magenta.pluginManager.getPlugin("CreditLite") != null) {
            val sm: ServicesManager = magenta.server.servicesManager
            val rsp = sm.getRegistration(CreditAPI::class.java)
            if (rsp != null) {
                eco = rsp.provider
                return true
            }
            return eco == null
        }
        return false
    }

    fun hasCredits(player: Player, amount: Double): Boolean {
        if (!setupCreditLite())
            throw CreditException(magenta.localeConfig.getMessage("magenta.missing.credits.economy"))

        return eco!!.has(player, amount)
    }

    fun getCredits(player: Player): Double {
        if (!setupCreditLite())
            throw CreditException(magenta.localeConfig.getMessage("magenta.missing.credits.economy"))

        return eco!!.getBalance(player)
    }

    fun depositCredits(player: Player, amount: Double)
    {
        if (!setupCreditLite())
            throw CreditException(magenta.localeConfig.getMessage("magenta.missing.credits.economy"))

        eco!!.deposit(player, amount)
    }

    fun withdrawCredits(player: Player, amount: Double) {
        if (!setupCreditLite())
            throw CreditException(magenta.localeConfig.getMessage("magenta.missing.credits.economy"))

        eco!!.withdraw(player, amount)
    }

}