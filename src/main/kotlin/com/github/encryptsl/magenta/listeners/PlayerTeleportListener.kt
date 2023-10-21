package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.account.PlayerAccount
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTeleportEvent

class PlayerTeleportListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onPlayerTeleport(event: PlayerTeleportEvent) {
        val player = event.player
        val playerAccount = PlayerAccount(magenta, player.uniqueId)

        if (playerAccount.jailManager.hasPunish() && playerAccount.isJailed()) return

        playerAccount.saveLastLocation(player)
    }

}