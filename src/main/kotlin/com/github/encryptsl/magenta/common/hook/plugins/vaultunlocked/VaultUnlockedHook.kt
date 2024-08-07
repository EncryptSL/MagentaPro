package com.github.encryptsl.magenta.common.hook.plugins.vaultunlocked

import com.github.encryptsl.kmono.lib.api.economy.Economy
import com.github.encryptsl.kmono.lib.api.economy.MissingEconomyException
import com.github.encryptsl.kmono.lib.api.hook.PluginHook
import com.github.encryptsl.magenta.Magenta
import org.bukkit.OfflinePlayer
import org.bukkit.plugin.ServicesManager
import java.math.BigDecimal

class VaultUnlockedHook(private val magenta: Magenta) : PluginHook("Vault"), Economy {

    private var eco: net.milkbowl.vault2.economy.Economy? = null
    private val exception = magenta.locale.getMessage("magenta.missing.vault.economy")
    private val pluginName = magenta.name

    override fun isPluginEnabled(): Boolean {
        if (magenta.pluginManager.getPlugin("Vault") != null) {
            val sm: ServicesManager = magenta.server.servicesManager
            val rsp = sm.getRegistration(net.milkbowl.vault2.economy.Economy::class.java)
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

        return eco!!.has(pluginName, player.uniqueId, null, currency, value)
    }


    override fun deposit(player: OfflinePlayer, currency: String, value: BigDecimal) {
        if (!isPluginEnabled())
            throw MissingEconomyException(exception)

        eco!!.deposit(pluginName, player.uniqueId, null, currency, value)
    }

    override fun withdraw(player: OfflinePlayer, currency: String, value: BigDecimal) {
        if (!isPluginEnabled())
            throw MissingEconomyException(exception)

        eco!!.withdraw(pluginName, player.uniqueId, null, currency, value)
    }

    override fun getBalance(player: OfflinePlayer, currency: String): BigDecimal {
        if(!isPluginEnabled())
            throw MissingEconomyException(exception)

        return eco!!.getBalance(pluginName, player.uniqueId, null, currency)
    }
}