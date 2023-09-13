package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.PlayerAccount
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent

class PlayerAsyncLogin(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onAsyncLogin(event: AsyncPlayerPreLoginEvent) {
        val player = event.uniqueId
        val playerAccount = PlayerAccount(magenta, player)
        playerAccount.createAccount()
    }

}