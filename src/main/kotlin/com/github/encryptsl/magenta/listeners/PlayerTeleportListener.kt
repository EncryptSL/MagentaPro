package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.PlayerAccount
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTeleportEvent

class PlayerTeleportListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onPlayerTeleport(event: PlayerTeleportEvent) {
        val player = event.player
        val playerAccount = PlayerAccount(magenta, player.uniqueId)

        playerAccount.getAccount().set("lastlocation.world-name", player.world.name)
        playerAccount.getAccount().set("lastlocation.x", player.location.x)
        playerAccount.getAccount().set("lastlocation.y", player.location.y)
        playerAccount.getAccount().set("lastlocation.z", player.location.z)
        playerAccount.getAccount().set("lastlocation.yaw", player.location.yaw)
        playerAccount.getAccount().set("lastlocation.pitch", player.location.pitch)
        playerAccount.save()
    }

}