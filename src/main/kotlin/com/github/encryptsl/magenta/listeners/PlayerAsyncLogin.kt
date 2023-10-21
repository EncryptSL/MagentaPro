package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.database.entity.LevelEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent

class PlayerAsyncLogin(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onAsyncLogin(event: AsyncPlayerPreLoginEvent) {
        val player = event.uniqueId
        if (event.loginResult != AsyncPlayerPreLoginEvent.Result.ALLOWED) return

        magenta.user.getUser(player)
        magenta.virtualLevel.createAccount(LevelEntity(event.name, player.toString(), 1, 0))
    }

}