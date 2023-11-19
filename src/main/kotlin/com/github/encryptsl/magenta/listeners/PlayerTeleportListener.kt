package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.account.UserAccount
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTeleportEvent

class PlayerTeleportListener(private val magenta: Magenta) : Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerTeleport(event: PlayerTeleportEvent) {
        val player = event.player
        val userAccount = UserAccount(magenta, player.uniqueId)

        if (userAccount.jailManager.hasPunish() && userAccount.isJailed()) return

        userAccount.saveLastLocation(player)
    }

}