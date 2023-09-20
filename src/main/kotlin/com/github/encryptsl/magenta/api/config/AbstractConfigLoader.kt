package com.github.encryptsl.magenta.api.config

import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import java.io.File

abstract class AbstractConfigLoader : Config {

    override fun createFromResources(configName: String, plugin: Plugin): AbstractConfigLoader {
        val file = File(plugin.dataFolder, configName)
        if (!file.exists()) {
            plugin.saveResource(configName, false)
        } else {
            plugin.logger.info("Configuration $configName exist !")
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