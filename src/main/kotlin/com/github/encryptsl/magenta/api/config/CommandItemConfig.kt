package com.github.encryptsl.magenta.api.config

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.scheduler.SchedulerMagenta
import com.github.encryptsl.magenta.common.utils.ConfigUtil
import org.bukkit.configuration.file.FileConfiguration

class CommandItemConfig(private val magenta: Magenta) {

    private val configUtil = ConfigUtil(magenta, "citems.yml")
    fun set(path: String, value: Any?) {
        SchedulerMagenta.doAsync(magenta) {
            getConfig().set(path, value)
            save()
        }
    }

    fun set(path: String, list: MutableList<Any>) {
        SchedulerMagenta.doAsync(magenta) {
            list.forEach { item ->
                getConfig().set(path, item)
            }
            save()
        }
    }

    fun createItem(value: String, sid: Int) {
        SchedulerMagenta.doAsync(magenta) {
            set("citems.$value.sid", sid)
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