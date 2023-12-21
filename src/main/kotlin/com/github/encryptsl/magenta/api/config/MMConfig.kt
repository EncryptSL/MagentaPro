package com.github.encryptsl.magenta.api.config

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ConfigUtil
import org.bukkit.configuration.file.FileConfiguration

class MMConfig(magenta: Magenta) {
    private val configUtil = ConfigUtil(magenta, "elitemobs/rewards.yml")

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