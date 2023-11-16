package com.github.encryptsl.magenta.common.hook.vault

import com.github.encryptsl.magenta.Magenta
import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.entity.Player
import org.bukkit.plugin.ServicesManager

class VaultHook(private val magenta: Magenta) {

    private var eco: Economy? = null
    private val exception = magenta.localeConfig.getMessage("magenta.missing.vault.economy")

     fun setupEconomy(): Boolean {
         if (magenta.pluginManager.getPlugin("Vault") != null) {
             val sm: ServicesManager = magenta.server.servicesManager
             val rsp = sm.getRegistration(Economy::class.java)
             if (rsp != null) {
                 eco = rsp.provider
                 return true
             }
             return eco == null
         }
         return false
    }

    fun getBalance(player: Player): Double
    {
        if(!setupEconomy())
            throw VaultException(exception)

        return eco!!.getBalance(player)
    }

    fun withdraw(player: Player, amount: Double): EconomyResponse {
        if(!setupEconomy())
            throw VaultException(exception)

        return eco!!.withdrawPlayer(player, amount)
    }

    fun deposit(player: Player, amount: Double): EconomyResponse {
        if(!setupEconomy())
            throw VaultException(exception)

        return eco!!.depositPlayer(player, amount)
    }

}