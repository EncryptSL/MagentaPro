package com.github.encryptsl.magenta.common.utils

import com.github.encryptsl.magenta.Magenta
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class ConfigUtil {

    var file: File
    private var config: FileConfiguration

    constructor(path: String) {
        this.file = File(Magenta.instance.dataFolder, path)
        this.config = YamlConfiguration.loadConfiguration(file)
    }

    fun reload() {
        try {
            config.load(file)
            Magenta.instance.logger.info("${file.name} is reloaded !")
        } catch (e : Exception) {
            Magenta.instance.logger.severe(e.message ?: e.localizedMessage)
        }
    }

    fun save() {
        try {
            config.save(file)
        } catch (e : Exception) {
            Magenta.instance.logger.severe(e.message ?: e.localizedMessage)
        }
    }
    fun getConfig(): FileConfiguration {
        return config
    }

}