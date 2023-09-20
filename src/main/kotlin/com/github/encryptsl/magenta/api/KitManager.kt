package com.github.encryptsl.magenta.api

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ModernText
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player

class KitManager(private val magenta: Magenta) {

    private val itemFactory = ItemFactory()

    fun giveKit(player: Player, kitName: String) {

        runCatching {
            magenta.kitConfig.getKit().getConfigurationSection("kits.$kitName")
        }.onSuccess { section ->
            section?.getStringList("items")?.forEach { item ->
                val split = item.split(" ")

                if (split[0].isNotEmpty() && split[1].isNotEmpty()) {
                    player.inventory.addItem(itemFactory.item(Material.getMaterial(item[0].toString())!!, item[1].toInt()))
                }
                if (split[2].isNotEmpty()) {
                    player.inventory.addItem(itemFactory.item(
                        Material.getMaterial(item[0].toString())!!, item[1].toInt(), Enchantment.getByKey(
                            NamespacedKey.fromString(item[2].toString()))!!, item[3].toInt()))
                }
                if (split[4].isNotEmpty()) {
                    player.inventory.addItem(itemFactory.item(
                        Material.getMaterial(item[0].toString())!!,
                        item[1].toInt(),
                        Enchantment.getByKey(NamespacedKey.fromString(item[2].toString()))!!,
                        item[3].toInt(),
                        listOf(ModernText.miniModernText(item[4].toString()))
                    ))
                }
            }
        }.onFailure { e ->
            player.sendMessage(e.message ?: e.localizedMessage)
            magenta.logger.severe(e.message ?: e.localizedMessage)
        }
    }
}