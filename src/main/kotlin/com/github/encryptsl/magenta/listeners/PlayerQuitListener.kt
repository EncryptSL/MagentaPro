package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.PlayerAccount
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PlayerQuitListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        val playerAccount = PlayerAccount(magenta, player.uniqueId)
        val account = playerAccount.getAccount()

        account.set("timestamps.logout", System.currentTimeMillis())
        account.set("logoutlocation.world-name", player.world.name)
        account.set("logoutlocation.x", player.location.x)
        account.set("logoutlocation.y", player.location.y)
        account.set("logoutlocation.z", player.location.z)
        account.set("logoutlocation.yaw", player.location.yaw)
        account.set("logoutlocation.pitch", player.location.pitch)
        playerAccount.save()
        playerAccount.reload()
    }

}