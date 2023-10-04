package com.github.encryptsl.magenta.common.hook

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.hook.nuvotifier.VotifierListener

class HookManager(private val magenta: Magenta) {

    /**
     * Method for check if plugin is installed
     * @param pluginName - String name of plugin is CaseSensitive
     * @return Boolean
     */
    private fun isPluginInstalled(pluginName: String): Boolean {
        return magenta.pluginManager.getPlugin(pluginName) != null
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