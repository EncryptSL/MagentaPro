package com.github.encryptsl.magenta.common.hook.vault

import com.github.encryptsl.kmono.lib.api.economy.MissingEconomyException
import com.github.encryptsl.kmono.lib.api.hook.PluginHook
import com.github.encryptsl.magenta.Magenta
import net.milkbowl.vault.economy.Economy
import org.bukkit.OfflinePlayer
import org.bukkit.plugin.ServicesManager
import java.math.BigDecimal

class VaultHook(private val magenta: Magenta) : PluginHook("Vault"), com.github.encryptsl.kmono.lib.api.economy.Economy {

    private var eco: Economy? = null
    private val exception = magenta.locale.getMessage("magenta.missing.vault.economy")

    override fun isPluginEnabled(): Boolean {
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

    override fun hasBalance(player: OfflinePlayer, value: BigDecimal): Boolean {
        if(!isPluginEnabled())
            throw MissingEconomyException(exception)

        return eco!!.has(player, value.toDouble())
    }

    override fun deposit(player: OfflinePlayer, value: BigDecimal) {
        if(!isPluginEnabled())
            throw MissingEconomyException(exception)

        eco!!.depositPlayer(player, value.toDouble())
    }

    override fun withdraw(player: OfflinePlayer, value: BigDecimal) {
        if(!isPluginEnabled())
            throw MissingEconomyException(exception)

        eco!!.withdrawPlayer(player, value.toDouble())
    }

    override fun getBalance(player: OfflinePlayer): BigDecimal {
        if(!isPluginEnabled())
            throw MissingEconomyException(exception)

        return BigDecimal.valueOf(eco!!.getBalance(player))
    }
}