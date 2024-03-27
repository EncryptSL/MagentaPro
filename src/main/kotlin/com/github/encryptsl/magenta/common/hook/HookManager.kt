package com.github.encryptsl.magenta.common.hook

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.hook.creditlite.CreditLiteHook
import com.github.encryptsl.magenta.common.hook.luckperms.LuckPermsAPI
import com.github.encryptsl.magenta.common.hook.mythicmobs.MythicMobsListener
import com.github.encryptsl.magenta.common.hook.nuvotifier.VotifierListener
import com.github.encryptsl.magenta.common.hook.oraxen.OraxenListener
import com.github.encryptsl.magenta.common.hook.placeholderapi.MagentaPlaceholderAPI
import com.github.encryptsl.magenta.common.hook.vault.VaultHook

class HookManager(private val magenta: Magenta) {

    /**
     * Method for check if plugin is installed
     * @param pluginName - String name of plugin is CaseSensitive
     * @return Boolean
     */
    private fun isPluginInstalled(pluginName: String): Boolean {
        return magenta.pluginManager.getPlugin(pluginName) != null && magenta.pluginManager.isPluginEnabled(pluginName)
    }

    fun hookMythicMobs() {
        if (isPluginInstalled("MythicMobs")) {
            magenta.logger.info("MythicMobs Found Hook Success")
            magenta.pluginManager.registerEvents(MythicMobsListener(magenta), magenta)
        } else {
            magenta.logger.warning("MythicMobs not found, you can't use damage rewarding !")
        }
    }

    fun hookOraxen() {
        if (isPluginInstalled("Oraxen")) {
            magenta.logger.info("Oraxen found, now you can allow or ban item in worlds.")
            magenta.pluginManager.registerEvents(OraxenListener(magenta), magenta)
        } else {
            magenta.logger.warning("Oraxen not found, you can't allow or ban item in worlds !")
        }
    }

    fun hookLuckPerms() {
        if (isPluginInstalled("LuckPerms") && LuckPermsAPI().setupLuckPerms()) {
            magenta.logger.info("LuckPerms found hook success !")
        } else {
            magenta.logger.warning("LuckPerms not found please use vault plugin.")
        }
    }

    fun hookVault() {
        if (isPluginInstalled("Vault") && VaultHook(magenta).setupEconomy()) {
            magenta.logger.info("Vault found hook success !")
        } else {
            magenta.logger.warning("Vault not found, please download !")
        }
    }

    fun hookCreditLite() {
        if (isPluginInstalled("CreditLite") && CreditLiteHook(magenta).setupCreditLite()) {
            magenta.logger.info("CreditLite found hook success !")
        } else {
            magenta.logger.warning("CreditLite not found, you can't use credit economy !")
        }
    }

    fun hookMiniPlaceholders() {
        if (isPluginInstalled("MiniPlaceholders")) {
            magenta.logger.info("MiniPlaceholders found, placeholders are registered !")
        } else {
            magenta.logger.warning("Warning plugin MiniPlaceholders not found !")
            magenta.logger.warning("Keep in mind without MiniPlaceholders, you can't use MagentaPro MiniPlaceholders.")
        }
    }
    fun hookPAPI() {
        if (isPluginInstalled("PlaceholderAPI")) {
            magenta.logger.info("PlaceholderAPI found, placeholders are registered !")
            MagentaPlaceholderAPI(magenta, "1.0.0").register()
        } else {
            magenta.logger.warning("Warning plugin PlaceholderAPI not found !")
            magenta.logger.warning("Keep in mind without PlaceholderAPI, you can't use MagentaPro PAPI Placeholders.")
        }
    }

    fun hookNuVotifier() {
        if (isPluginInstalled("Votifier")) {
            magenta.logger.info("NuVotifier found hook success !")
            magenta.pluginManager.registerEvents(VotifierListener(magenta), magenta)
        } else {
            magenta.logger.warning("NuVotifier not found, rewarding from voting not working now !")
        }
    }


}