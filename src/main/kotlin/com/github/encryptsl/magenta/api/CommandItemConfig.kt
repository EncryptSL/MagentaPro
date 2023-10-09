package com.github.encryptsl.magenta.api

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ConfigUtil
import org.bukkit.configuration.file.FileConfiguration

class CommandItemConfig(private val magenta: Magenta) {

    private val configUtil = ConfigUtil(magenta, "citems.yml")
    fun set(path: String, value: Any?) {
        magenta.schedulerMagenta.runTaskAsync(magenta) {
            getConfig().set(path, value)
            save()
        }
    }

    fun set(path: String, list: MutableList<Any>) {
        magenta.schedulerMagenta.runTaskAsync(magenta) {
            list.forEach { item ->
                getConfig().set(path, item)
            }
            save()
        }
    }

    fun createSection(value: String) {
        magenta.schedulerMagenta.runTaskAsync(magenta) {
            getConfig().createSection(value)
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