package com.github.encryptsl.magenta.api.config

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ConfigUtil
import org.bukkit.configuration.file.FileConfiguration

class UniversalConfig(magenta: Magenta, type: String) {

    private val configUtil = ConfigUtil(magenta, type)

    fun fileExist(): Boolean
    {
        return configUtil.file.isFile
    }

    fun reload() {
        configUtil.reload()
    }

    fun save() {
        configUtil.save()
    }

    fun getConfig(): FileConfiguration {
        return configUtil.getConfig()
    }

}