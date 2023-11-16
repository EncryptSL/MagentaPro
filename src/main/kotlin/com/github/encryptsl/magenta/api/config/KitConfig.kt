package com.github.encryptsl.magenta.api.config

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ConfigUtil
import org.bukkit.configuration.file.FileConfiguration

class KitConfig(magenta: Magenta) {

    private val configUtil = ConfigUtil(magenta, "kits.yml")

    fun getKit(): FileConfiguration {
        return configUtil.getConfig()
    }

    fun reload() {
        configUtil.reload()
    }

    fun save() {
        configUtil.save()
    }
}