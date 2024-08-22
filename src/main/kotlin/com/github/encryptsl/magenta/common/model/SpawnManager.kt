package com.github.encryptsl.magenta.common.model

import com.github.encryptsl.kmono.lib.extensions.playSound
import com.github.encryptsl.magenta.Magenta
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

class SpawnManager(private val magenta: Magenta) {

    fun spawn(player: Player) {
        Magenta.scheduler.impl.teleportAsync(player, geSpawnLocation())
    }

    fun spawnAsync(player: Player): CompletableFuture<Void>? {
        return Magenta.scheduler.impl.teleportAsync(player, geSpawnLocation())?.thenAccept {
            playSound(player, magenta.config.getString("void-spawn.sound").toString(), 5f, 1f)
        }
    }

    fun geSpawnLocation(): Location {
        return magenta.spawnConfig.getConfig().getLocation("spawn") ?: Bukkit.getWorlds().first().spawnLocation
    }

}