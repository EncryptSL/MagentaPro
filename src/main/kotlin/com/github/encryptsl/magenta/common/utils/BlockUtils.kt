package com.github.encryptsl.magenta.common.utils

import com.github.encryptsl.kmono.lib.api.ModernText
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.CreatureSpawner
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object BlockUtils {

    fun dropSpawner(block: Block, spawnerName: String)
    {
        val spawnerItem = ItemStack(Material.SPAWNER)
        val spawnerMeta = spawnerItem.itemMeta

        val spawner: CreatureSpawner = block.state as CreatureSpawner
        val entityType = spawner.spawnedType ?: return
        spawnerMeta.displayName(ModernText.miniModernText(spawnerName.replace("<entity>", entityType.name)))
        spawnerItem.setItemMeta(spawnerMeta)

        block.world.dropItemNaturally(block.location, spawnerItem)
    }

    fun updateSpawner(block: Block, player: Player) {
        val itemInHand = player.inventory.itemInMainHand

        if (itemInHand.type != Material.SPAWNER) return

        val spawnerItemMeta = itemInHand.itemMeta
        if (spawnerItemMeta != null) {
            val spawnerName = spawnerItemMeta.displayName() ?: return
            val entityName = ModernText.convertComponentToText(spawnerName).split(" ")
            val entityType = EntityType.valueOf(entityName[1])
            val spawner: CreatureSpawner = block.state as CreatureSpawner
            spawner.spawnedType = entityType
            spawner.update()
        }
    }

}