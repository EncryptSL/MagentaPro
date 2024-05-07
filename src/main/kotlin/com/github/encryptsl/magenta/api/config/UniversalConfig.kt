package com.github.encryptsl.magenta.api.config

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ConfigUtil
import fr.euphyllia.energie.model.SchedulerType
import org.bukkit.configuration.file.FileConfiguration

class UniversalConfig(type: String) {

    private val configUtil = ConfigUtil(type)

    fun exists(): Boolean
    {
        return configUtil.file.exists()
    }

    fun set(path: String, value: Any?, sync: Boolean = true) {
        if (sync) {
            getConfig().set(path, value)
            save()
            return
        }
        Magenta.scheduler.runTask(SchedulerType.ASYNC) {
            getConfig().set(path, value)
            save()
        }
    }

    fun set(path: String, list: MutableList<Any>, sync: Boolean = true) {
        val methodSynchronization = if (sync) SchedulerType.SYNC else SchedulerType.ASYNC
        Magenta.scheduler.runTask(methodSynchronization) {
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