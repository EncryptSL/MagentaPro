package com.github.encryptsl.magenta.common.hook

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.hook.creditlite.CreditLiteHook
import com.github.encryptsl.magenta.common.hook.nuvotifier.VotifierListener
import com.github.encryptsl.magenta.common.hook.placeholderapi.MagentaPlaceholderAPI
import com.github.encryptsl.magenta.common.hook.vault.VaultHook

class HookManager(private val magenta: Magenta) {

    /**
     * Method for check if plugin is installed
     * @param pluginName - String name of plugin is CaseSensitive
     * @return Boolean
     */
    private fun isPluginInstalled(pluginName: String): Boolean {
        return magenta.pluginManager.getPlugin(pluginName) != null
    }

    fun hookVault() {
        if (isPluginInstalled("VaultAPI")) {
            magenta.logger.info("###################################")
            magenta.logger.info("# Vault registered like a service #")
            magenta.logger.info("###################################")
            VaultHook(magenta).setupEconomy()
        } else {
            magenta.logger.info("###################################")
            magenta.logger.info("#         Vault not Found         #")
            magenta.logger.info("#     please download vault api   #")
            magenta.logger.info("###################################")
        }
    }

    fun hookCreditLite() {
        if (isPluginInstalled("CreditLite")) {
            magenta.logger.info("###################################")
            magenta.logger.info("# CreditLite registered like a service#")
            magenta.logger.info("###################################")
            CreditLiteHook(magenta).setupCreditLite()
        } else {
            magenta.logger.info("###################################")
            magenta.logger.info("#       CreditLite not Found      #")
            magenta.logger.info("#     please download vault api   #")
            magenta.logger.info("###################################")
        }
    }

    fun hookPAPI() {
        if (isPluginInstalled("PlaceholderAPI")) {
            magenta.logger.info("###################################")
            magenta.logger.info("#       PlaceholderAPI Found      #")
            magenta.logger.info("#     You can register service    #")
            magenta.logger.info("###################################")
            MagentaPlaceholderAPI(magenta, "1.0.0").register()

        } else {
            magenta.logger.info("###################################")
            magenta.logger.info("#    PlaceholderAPI Not Found     #")
            magenta.logger.info("#   You can't register service    #")
            magenta.logger.info("###################################")
        }
    }

    fun hookNuVotifier() {
        if (isPluginInstalled("Votifier")) {
            magenta.logger.info("###################################")
            magenta.logger.info("#         NuVotifier Found        #")
            magenta.logger.info("#     You can now use voting      #")
            magenta.logger.info("###################################")
            magenta.pluginManager.registerEvents(VotifierListener(magenta), magenta)
        } else {
            magenta.logger.info("###################################")
            magenta.logger.info("#       NuVotifier not found      #")
            magenta.logger.info("###################################")
        }
    }


}