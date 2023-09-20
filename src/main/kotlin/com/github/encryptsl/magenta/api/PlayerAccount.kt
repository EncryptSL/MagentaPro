package com.github.encryptsl.magenta.api

import com.github.encryptsl.magenta.Magenta
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.*

class PlayerAccount(private val magenta: Magenta, private val uuid: UUID) {

    private val yaml: YamlConfiguration = YamlConfiguration()
    private val file = File("${magenta.dataFolder}/players/", "{}.yml".format(uuid))

    init {
        yaml.load(file)
    }

    fun createAccount() {
        magenta.configLoader.create("/players/{}.yml".format(uuid))
    }

    fun getAccount(): YamlConfiguration {
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