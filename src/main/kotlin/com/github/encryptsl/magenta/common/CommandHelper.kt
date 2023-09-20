package com.github.encryptsl.magenta.common

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.PlayerAccount
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent

class CommandHelper(private val magenta: Magenta) {

    fun teleportAll(sender: Player, players: MutableCollection<out Player>) {
        players.forEach {
            it.teleportAsync(sender.location)
        }
    }

    fun teleportOffline(sender: Player, target: OfflinePlayer) {
        val playerAccount = PlayerAccount(magenta, target.uniqueId)
        val world = playerAccount.getAccount().getString("lastlocation.world-name") ?: return
        val x = playerAccount.getAccount().getDouble("lastlocation.x")
        val y = playerAccount.getAccount().getDouble("lastlocation.y")
        val z = playerAccount.getAccount().getDouble("lastlocation.z")
        val pitch = playerAccount.getAccount().getDouble("lastlocation.pitch")
        val yaw = playerAccount.getAccount().getDouble("lastlocation.yaw")

        sender.teleport(Location(Bukkit.getWorld(world), x, y, z, pitch.toFloat(), yaw.toFloat()), PlayerTeleportEvent.TeleportCause.COMMAND)
    }

}