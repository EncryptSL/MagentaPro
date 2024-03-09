package com.github.encryptsl.magenta.api.config

import com.github.encryptsl.magenta.api.scheduler.SchedulerMagenta
import com.github.encryptsl.magenta.common.utils.ConfigUtil
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.Plugin

class UniversalConfig(val plugin: Plugin, type: String) {

    private val configUtil = ConfigUtil(plugin, type)

    fun fileExist(): Boolean
    {
        return configUtil.file.isFile
    }

    fun set(path: String, value: Any?) {
        SchedulerMagenta.doAsync(plugin) {
            getConfig().set(path, value)
            save()
        }
    }

    fun set(path: String, list: MutableList<Any>) {
        SchedulerMagenta.doAsync(plugin) {
            for (el in list) { getConfig().set(path, el) }
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