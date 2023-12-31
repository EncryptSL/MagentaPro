package com.github.encryptsl.magenta.api.config.loader

import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import java.io.File

abstract class AbstractConfigLoader : Config {

    override fun createFromResources(configName: String, plugin: Plugin): AbstractConfigLoader {
        try {
            val file = File(plugin.dataFolder, configName)
            if (!file.exists()) {
                plugin.saveResource(configName, false)
            } else {
                plugin.logger.info("Configuration $configName exist !")
            }
        } catch (e : IllegalArgumentException) {
            plugin.logger.info(e.message ?: e.localizedMessage)
        }
        return this
    }

    override fun checkDependency(pluginName: String, plugin: Plugin): AbstractConfigLoader {
        if (Bukkit.getPluginManager().getPlugin(pluginName) == null) {
            plugin.logger.info("Plugin $pluginName not found")
            Bukkit.getPluginManager().disablePlugin(plugin)
        }
        return this
    }
}