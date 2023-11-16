package com.github.encryptsl.magenta.api.config

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ConfigUtil
import org.bukkit.configuration.file.FileConfiguration

class JailConfig(magenta: Magenta) {

    private val configUtil = ConfigUtil(magenta, "jails.yml")

    fun getJail(): FileConfiguration {
        return configUtil.getConfig()
    }

    fun reload() {
        configUtil.reload()
    }

    fun save() {
        configUtil.save()
    }
}