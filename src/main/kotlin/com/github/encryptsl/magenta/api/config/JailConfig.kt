package com.github.encryptsl.magenta.api.config

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ConfigUtil
import org.bukkit.configuration.file.FileConfiguration

class JailConfig(private val magenta: Magenta) {

    private val configUtil = ConfigUtil(magenta, "jails.yml")

    fun getJail(): FileConfiguration {
        return configUtil.getConfig()
    }

    fun reload() {
        runCatching {
            configUtil.reload()
        }.onSuccess {
            magenta.logger.info("${configUtil.file.name} is reloaded !")
        }.onFailure { e ->
            magenta.logger.severe(e.message ?: e.localizedMessage)
        }
    }

    fun save() {
        runCatching {
            configUtil.save()
        }.onSuccess {
            magenta.logger.info("${configUtil.file.name} is saved now !")
        }.onFailure { e ->
            magenta.logger.severe(e.message ?: e.localizedMessage)
        }
    }
}