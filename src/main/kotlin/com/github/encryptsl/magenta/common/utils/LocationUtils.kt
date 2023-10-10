package com.github.encryptsl.magenta.common.utils

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import java.util.*


object LocationUtils {
    fun getRandomLocation(player: Player): Location {
        val world = player.world
        val rand = Random()
        // Adjust the range max for the maximum X and Z value, and the range min for the minimum X and Z value.
        val rangeMax = 2500
        val rangeMin = -2500
        val x: Int = rand.nextInt(rangeMax - rangeMin + 1) + rangeMin
        val z: Int = rand.nextInt(rangeMax - rangeMin + 1) + rangeMin
        val y = world.getHighestBlockYAt(x, z)

        return Location(world, x.toDouble(), y.toDouble(), z.toDouble()).add(0.5, 1.0, 0.5)
    }

    fun isSafe(block: Block): Boolean {
        // Check if the player has space to stand in.
        return if (block.getType() !== Material.AIR && block.getRelative(BlockFace.UP)
            .getType() !== Material.AIR) false
        else block.getRelative(BlockFace.DOWN).getType() !== Material.LAVA && block.getRelative(BlockFace.DOWN)
            .getType() !== Material.WATER
    }
}