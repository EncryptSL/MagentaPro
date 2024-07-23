package com.github.encryptsl.magenta.common.hook.vault

import com.github.encryptsl.kmono.lib.api.economy.Economy
import com.github.encryptsl.kmono.lib.api.economy.MissingEconomyException
import com.github.encryptsl.kmono.lib.api.hook.PluginHook
import com.github.encryptsl.magenta.Magenta
import org.bukkit.OfflinePlayer
import org.bukkit.plugin.ServicesManager
import java.math.BigDecimal

class VaultHook(private val magenta: Magenta) : PluginHook("Vault"), Economy {

    private var eco: net.milkbowl.vault.economy.Economy? = null
    private val exception = magenta.locale.getMessage("magenta.missing.vault.economy")

    override fun isPluginEnabled(): Boolean {
        if (magenta.pluginManager.getPlugin("Vault") != null) {
            val sm: ServicesManager = magenta.server.servicesManager
            val rsp = sm.getRegistration(net.milkbowl.vault.economy.Economy::class.java)
            if (rsp != null) {
                eco = rsp.provider
                return true
            }
            return eco == null
        }
        return false
    }

    override fun hasBalance(player: OfflinePlayer, currency: String, value: BigDecimal): Boolean {
        if(!isPluginEnabled())
            throw MissingEconomyException(exception)

        return eco!!.has(player, value.toDouble())
    }

    override fun deposit(player: OfflinePlayer, currency: String, value: BigDecimal) {
        if(!isPluginEnabled())
            throw MissingEconomyException(exception)

        eco!!.depositPlayer(player, value.toDouble())
    }

    override fun withdraw(player: OfflinePlayer, currency: String, value: BigDecimal) {
        if(!isPluginEnabled())
            throw MissingEconomyException(exception)

        eco!!.withdrawPlayer(player, value.toDouble())
    }

    override fun getBalance(player: OfflinePlayer, currency: String): BigDecimal {
        if(!isPluginEnabled())
            throw MissingEconomyException(exception)

        return BigDecimal.valueOf(eco!!.getBalance(player))
    }
}