package com.github.encryptsl.magenta.common.utils

import com.github.encryptsl.magenta.Magenta
import java.time.Duration
import java.util.*


class AfkUtils(private val magenta: Magenta) {

    private val lastActivity: MutableMap<UUID, Long> = HashMap()

    fun setTime(uuid: UUID) {
        lastActivity[uuid] = System.currentTimeMillis()
    }

    private fun setTime(uuid: UUID, time: Long) {
        lastActivity[uuid] = time
    }
    private fun forceAfk(uuid: UUID, boolean: Boolean) {
        val user = magenta.user.getUser(uuid)
        user.set("afk", boolean)
    }

    fun clear(uuid: UUID) {
        lastActivity.remove(uuid)
    }

    fun clear() {
        lastActivity.clear()
    }

    fun isAfk(uuid: UUID, isForcedAfk: Boolean = false): Boolean {
        val user = magenta.user.getUser(uuid)
        val millis = Duration.ofMinutes(magenta.config.getLong("auto-afk")).toMillis()

        val lastActivity: Long? = lastActivity[uuid]

        if (!isForcedAfk) {
            if (lastActivity != null && System.currentTimeMillis() - lastActivity >= millis || lastActivity == -1L) {
                forceAfk(uuid, true)
            } else {
                forceAfk(uuid, false)
            }
        } else {
            if (user.isAfk()) {
                forceAfk(uuid, false)
                setTime(uuid)
            } else {
                setTime(uuid, -1L)
                forceAfk(uuid, true)
            }
        }
        return user.isAfk()
    }

}