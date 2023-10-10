package com.github.encryptsl.magenta.common.utils

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.CreatureSpawner
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object BlockUtils {

    fun dropSpawner(block: Block)
    {
        val spawnerItem = ItemStack(Material.SPAWNER)
        val spawnerMeta = spawnerItem.itemMeta

        val spawner: CreatureSpawner = block.state as CreatureSpawner
        val entityType = spawner.spawnedType ?: return
        spawnerMeta.displayName(ModernText.miniModernText("<red>SPAWNER <yellow>${entityType.name}"))
        spawnerItem.setItemMeta(spawnerMeta)

        block.world.dropItemNaturally(block.location, spawnerItem)
    }

    fun updateSpawner(block: Block, player: Player) {
        val itemInHand = player.inventory.itemInMainHand

        if (itemInHand.type == Material.SPAWNER && itemInHand.hasItemMeta()) {
            val spawnerItemMeta = itemInHand.itemMeta
            if (spawnerItemMeta.hasDisplayName()) {
                val itemName = PlainTextComponentSerializer.plainText().serialize(spawnerItemMeta.displayName()!!)
                val entityName = itemName.split(" ")
                val entityType = EntityType.valueOf(entityName[1])

                val spawnerBlock: Block = block

                val spawner: CreatureSpawner = spawnerBlock.state as CreatureSpawner
                spawner.spawnedType = entityType
                spawner.update()
            }
        }
    }

}