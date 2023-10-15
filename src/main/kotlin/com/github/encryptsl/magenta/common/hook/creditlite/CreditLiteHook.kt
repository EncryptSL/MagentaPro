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

    fun getCredits(player: Player): Double {
        if (!setupCreditLite())
            throw CreditException("")

        return eco?.getBalance(player) ?: 0.0
    }

    fun depositeCredits(player: Player, amount: Double)
    {
        if (!setupCreditLite())
            throw CreditException("")

        eco?.depositMoney(player, amount)
    }

    fun withdrawCredits(player: Player, amount: Double) {
        if (!setupCreditLite())
            throw CreditException("")

        eco?.withDrawMoney(player, amount)
    }

}