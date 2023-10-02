package com.github.encryptsl.magenta.common.tasks

import com.github.encryptsl.magenta.Magenta
import org.bukkit.entity.Player

class PlayerAfkTask(private val magenta: Magenta) : Runnable {
    override fun run() {

        magenta.server.onlinePlayers.filter { player: Player -> magenta.afk.isAfk(player.uniqueId) }.forEach { player ->
            if (!player.hasPermission("magenta.afk.auto")) return
            magenta.teamIntegration.setAfk(player)
        }

    }
}