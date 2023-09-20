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

        account.set("lastlocation.world-name", player.world.name)
        account.set("lastlocation.x", player.location.x)
        account.set("lastlocation.y", player.location.y)
        account.set("lastlocation.z", player.location.z)
        account.set("lastlocation.yaw", player.location.yaw)
        account.set("lastlocation.pitch", player.location.pitch)
        playerAccount.save()
        playerAccount.reload()
    }

}