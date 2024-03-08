package com.github.encryptsl.magenta.api.config

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.scheduler.SchedulerMagenta
import com.github.encryptsl.magenta.common.utils.ConfigUtil
import org.bukkit.configuration.file.FileConfiguration

class UniversalConfig(val magenta: Magenta, type: String) {

    private val configUtil = ConfigUtil(magenta, type)

    fun fileExist(): Boolean
    {
        return configUtil.file.isFile
    }

    fun set(path: String, value: Any?) {
        SchedulerMagenta.doAsync(magenta) {
            getConfig().set(path, value)
            save()
        }
    }

    fun set(path: String, list: MutableList<Any>) {
        SchedulerMagenta.doAsync(magenta) {
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