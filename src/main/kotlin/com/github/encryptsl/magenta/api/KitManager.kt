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
            magenta.kitConfig.getKit().getConfigurationSection("kits")
        }.onSuccess { section ->
            val k = section?.get("kits.$kitName") as HashMap<String, Any>
            val items = k["items"] as List<HashMap<String, Any>>
            items.forEach { item ->
                println(item["id"])
            }
        }.exceptionOrNull()
    }

    fun createKit(kitName: String) {
        magenta.kitConfig.getKit().getConfigurationSection("kits.$kitName")?.set("", "")
    }
}