package com.github.encryptsl.magenta.common.utils

import com.github.encryptsl.magenta.Magenta
import java.time.Duration
import java.util.*


class AfkUtils(private val magenta: Magenta) {

    private val lastActivity: MutableMap<UUID, Long> = HashMap()

    fun setTime(uuid: UUID) {
        lastActivity[uuid] = System.currentTimeMillis()
    }

    fun forceAfk(uuid: UUID, boolean: Boolean) {
        val user = magenta.user.getUser(uuid)
        user.set("afk", boolean)
    }

    fun isAfk(uuid: UUID): Boolean {
        val millis = Duration.ofMinutes(magenta.config.getLong("auto-afk")).toMillis()

        val lastActivity: Long? = lastActivity[uuid]

        return lastActivity != null && System.currentTimeMillis() - lastActivity > millis || magenta.user.getUser(uuid).isAfk()
    }

}