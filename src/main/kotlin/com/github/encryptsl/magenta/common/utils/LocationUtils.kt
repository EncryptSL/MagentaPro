package com.github.encryptsl.magenta.common.utils

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.plugin.Plugin
import java.util.concurrent.ThreadLocalRandom


object LocationUtils {
    @JvmStatic
    fun generateLocation(plugin: Plugin, world: World): Location {
        val unsafeMaterials: List<String> = plugin.config.getStringList("rtp.unsafeBlocks")
        val negative: Boolean = plugin.config.getBoolean("rtp.limits.negative")
        val worldBorder: Boolean = plugin.config.getBoolean("rtp.limits.worldBorder")
        val xLimit =
            if (worldBorder) world.worldBorder.size.toInt() / 2 else plugin.config.getInt("rtp.limits.xLimit")
        val zLimit =
            if (worldBorder) world.worldBorder.size.toInt() / 2 else plugin.config.getInt("rtp.limits.zLimit")
        val random = ThreadLocalRandom.current()
        while (true) {
            val x = if (negative) random.nextInt(xLimit * 2) - xLimit else random.nextInt(xLimit)
            val z = if (negative) random.nextInt(zLimit * 2) - zLimit else random.nextInt(zLimit)
            val generatedBlock = world.getHighestBlockAt(x, z)
            val higherBlock = world.getBlockAt(x, generatedBlock.y + 2, z)
            if (unsafeMaterials.contains(generatedBlock.blockData.material.name) || unsafeMaterials.contains(higherBlock.blockData.material.name)) continue
            return Location(world, x + .5, (generatedBlock.y + 1).toDouble(), z + .5)
        }
    }

    fun isSafe(block: Block): Boolean {
        // Check if the player has space to stand in.
        return if (block.getType() !== Material.AIR && block.getRelative(BlockFace.UP)
            .getType() !== Material.AIR) false
        else block.getRelative(BlockFace.DOWN).getType() !== Material.LAVA && block.getRelative(BlockFace.DOWN)
            .getType() !== Material.WATER
    }
}