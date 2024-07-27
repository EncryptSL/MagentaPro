package com.github.encryptsl.magenta.common.model

import com.github.encryptsl.magenta.Magenta
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

class SpawnManager(private val magenta: Magenta) {

    fun spawn(player: Player) {
        geSpawntLocation()?.let { Magenta.scheduler.impl.teleportAsync(player, it) }
    }

    fun spawnAsync(player: Player): CompletableFuture<Boolean>? {
        return geSpawntLocation()?.let { Magenta.scheduler.impl.teleportAsync(player, it) }
    }

    private fun geSpawntLocation(): Location? {
        return magenta.spawnConfig.getConfig().getLocation("spawn")
    }

}