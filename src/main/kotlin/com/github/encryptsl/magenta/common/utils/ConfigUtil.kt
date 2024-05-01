package com.github.encryptsl.magenta.common.utils

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.File

class ConfigUtil {

    var file: File
    private var config: FileConfiguration
    private lateinit var plugin: Plugin

    constructor(plugin: Plugin, path: String) : this("${plugin.dataFolder.absolutePath}/$path") {
        this.plugin = plugin
    }

    constructor(path: String) {
        this.file = File(path)
        this.config = YamlConfiguration.loadConfiguration(file)
    }

    fun reload() {
        try {
            config.load(file)
            plugin.logger.info("${file.name} is reloaded !")
        } catch (e : Exception) {
            plugin.logger.severe(e.message ?: e.localizedMessage)
        }
    }

    fun save() {
        try {
            config.save(file)
        } catch (e : Exception) {
            plugin.logger.severe(e.message ?: e.localizedMessage)
        }
    }
    fun getConfig(): FileConfiguration {
        return config
    }

}