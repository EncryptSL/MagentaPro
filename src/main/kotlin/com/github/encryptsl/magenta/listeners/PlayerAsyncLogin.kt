package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.database.entity.LevelEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent

class PlayerAsyncLogin(private val magenta: Magenta) : Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    fun onAsyncLogin(event: AsyncPlayerPreLoginEvent) {
        val player = event.uniqueId
        if (event.loginResult != AsyncPlayerPreLoginEvent.Result.ALLOWED) return
        magenta.virtualLevel.createAccount(LevelEntity(event.name, player.toString(), 1, 0))
    }

}