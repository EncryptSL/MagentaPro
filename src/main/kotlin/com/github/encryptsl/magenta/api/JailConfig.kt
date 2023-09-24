package com.github.encryptsl.magenta.api

import com.github.encryptsl.magenta.Magenta
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class JailConfig(private val magenta: Magenta) {

    private val yaml: YamlConfiguration = YamlConfiguration()
    private val file = File("${magenta.dataFolder}/LiteEco/", "jails.yml")

    init {
        yaml.load(file)
    }

    fun createConfig() {
        magenta.configLoader.create("/jails.yml")
    }

    fun getJail(): YamlConfiguration {
        return yaml
    }

    fun reload() {
        runCatching {
            yaml.load(file)
        }.onSuccess {
            magenta.logger.info("${file.name} is reloaded !")
        }.onFailure { e ->
            magenta.logger.severe(e.message ?: e.localizedMessage)
        }
    }

    fun save() {
        runCatching {
            yaml.save(file)
        }.onSuccess {
            magenta.logger.info("${file.name} is saved now !")
        }.onFailure { e ->
            magenta.logger.severe(e.message ?: e.localizedMessage)
        }
    }
}