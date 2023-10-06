package com.github.encryptsl.magenta.common.hook

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.hook.nuvotifier.VotifierListener
import com.github.encryptsl.magenta.common.hook.placeholderapi.MagentaPlaceholderAPI

class HookManager(private val magenta: Magenta) {

    /**
     * Method for check if plugin is installed
     * @param pluginName - String name of plugin is CaseSensitive
     * @return Boolean
     */
    private fun isPluginInstalled(pluginName: String): Boolean {
        return magenta.pluginManager.getPlugin(pluginName) != null
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