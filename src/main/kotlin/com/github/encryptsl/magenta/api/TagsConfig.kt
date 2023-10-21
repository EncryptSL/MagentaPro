package com.github.encryptsl.magenta.api

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ConfigUtil
import org.bukkit.configuration.file.FileConfiguration

class TagsConfig(private val magenta: Magenta) {
    private val configUtil = ConfigUtil(magenta, "tags.yml")

    fun set(path: String, value: Any?) {
        magenta.schedulerMagenta.doAsync(magenta) {
            getConfig().set(path, value)
            save()
        }
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