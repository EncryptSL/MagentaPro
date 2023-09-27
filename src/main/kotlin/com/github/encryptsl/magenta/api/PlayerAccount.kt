package com.github.encryptsl.magenta.api

import com.github.encryptsl.magenta.Magenta
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.IOException
import java.util.*

class PlayerAccount(private val magenta: Magenta, private val uuid: UUID) {

    private var yaml: FileConfiguration = YamlConfiguration()
    private val file = File("${magenta.dataFolder}/players/", "$uuid.yml")

    init {
        yaml.load(file)
    }

    fun createAccount() {
        if (!file.exists()) {
            try {
                if (!file.mkdir()) file.mkdir()
                file.createNewFile()
            } catch ( e : IOException) {
                magenta.logger.warning("${this::class.java.simpleName} ${uuid}.yml exist ")
            }
        }
    }

    fun getAccount(): FileConfiguration {
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