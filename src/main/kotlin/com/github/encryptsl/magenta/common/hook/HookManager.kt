package com.github.encryptsl.magenta.common.hook

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.hook.luckperms.LuckPermsAPI
import com.github.encryptsl.magenta.common.hook.miniplaceholder.MagentaMiniPlaceholders
import com.github.encryptsl.magenta.common.hook.mythicmobs.MythicMobsListener
import com.github.encryptsl.magenta.common.hook.nuvotifier.VotifierListener
import com.github.encryptsl.magenta.common.hook.oraxen.OraxenListener
import com.github.encryptsl.magenta.common.hook.placeholderapi.MagentaPlaceholderAPI
import com.github.encryptsl.magenta.common.hook.plugins.vaultunlocked.VaultUnlockedHook
import org.bukkit.Bukkit

class HookManager(private val magenta: Magenta) {

    fun isPluginEnabled(pluginName: String): Boolean {
        return Bukkit.getPluginManager().getPlugin(pluginName) != null || Bukkit.getPluginManager().isPluginEnabled(pluginName)
    }

    fun hookPlugins() {
        MythicMobsListener(magenta).runIfSuccess {
            magenta.logger.info("MythicMobs Found Hook Success")
            magenta.pluginManager.registerEvents(MythicMobsListener(magenta), magenta)
        }.runIfElse {
            magenta.logger.warning("MythicMobs not found, you can't use damage rewarding !")
        }
        OraxenListener(magenta).runIfSuccess {
            magenta.logger.info("Oraxen found, now you can allow or ban item in worlds.")
            magenta.pluginManager.registerEvents(OraxenListener(magenta), magenta)
        }.runIfElse {
            magenta.logger.warning("Oraxen not found, you can't allow or ban item in worlds !")
        }
        LuckPermsAPI().runIfSuccess {
            magenta.logger.info("LuckPerms found hook success !")
        }.runIfElse {
            magenta.logger.warning("LuckPerms not found please use vault plugin.")
        }
        VaultUnlockedHook(magenta).runIfSuccess {
            magenta.logger.info("VaultUnlocked found hook success !")
        }.runIfElse {
            magenta.logger.warning("VaultUnlocked not found, please download !")
        }
        MagentaMiniPlaceholders(magenta).runIfSuccess {
            magenta.logger.info("MiniPlaceholders found, placeholders are registered !")
            MagentaMiniPlaceholders(magenta).register()
        }.runIfElse {
            magenta.logger.warning("Warning plugin MiniPlaceholders not found !")
            magenta.logger.warning("Keep in mind without MiniPlaceholders, you can't use MagentaPro MiniPlaceholders.")
        }
        VotifierListener(magenta).runIfSuccess {
            magenta.logger.info("NuVotifier found hook success !")
            magenta.pluginManager.registerEvents(VotifierListener(magenta), magenta)
        }.runIfElse {
            magenta.logger.warning("NuVotifier not found, rewarding from voting not working now !")
        }
        hookPAPI()
    }
    private fun hookPAPI() {
        if (isPluginEnabled("PlaceholderAPI")) {
            magenta.logger.info("PlaceholderAPI found, placeholders are registered !")
            MagentaPlaceholderAPI(magenta, "1.0.0").register()
        } else {
            magenta.logger.warning("Warning plugin PlaceholderAPI not found !")
            magenta.logger.warning("Keep in mind without PlaceholderAPI, you can't use MagentaPro PAPI Placeholders.")
        }
    }
}