package com.github.encryptsl.magenta.common.tasks

import com.github.encryptsl.magenta.Magenta

class PlayerAfkTask(private val magenta: Magenta) : Runnable {
    override fun run() {
        val p = magenta.server.onlinePlayers.find { p -> magenta.afk.isAfk(p.uniqueId) } ?: return
        if (!p.hasPermission("magenta.afk.auto")) return
        magenta.afk.forceAfk(p.uniqueId, true)
    }
}