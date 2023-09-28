package com.github.encryptsl.magenta.api

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.exceptions.KitNotFoundException
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class KitManager(private val magenta: Magenta) {

    fun giveKit(player: Player, kitName: String) {
        val inv: Inventory = player.inventory

        if (!magenta.kitConfig.getKit().contains("kits.$kitName"))
            throw KitNotFoundException(magenta.localeConfig.getMessage("magenta.command.kit.error.not.exist"))

        for (material in Material.entries) {
            if (magenta.kitConfig.getKit().contains("kits.$kitName.items.${material.name.lowercase()}")) {
                val amount: Int = magenta.kitConfig.getKit().getInt(("kits.$kitName.items.${material.name.lowercase()}.amount"))
                val item = ItemStack(material, amount)
                val meta: ItemMeta = item.itemMeta
                if (magenta.kitConfig.getKit().contains("kits.$kitName.items.${material.name.lowercase()}.meta.displayName")) {
                    meta.displayName(
                        ModernText.miniModernText(
                            magenta.kitConfig.getKit()
                                .getString("kits.$kitName.items.${material.name.lowercase()}.meta.displayName").toString()
                        )
                    )
                }
                if (magenta.kitConfig.getKit().contains("kits.$kitName.items.${material.name.lowercase()}.meta.lore")) {
                    val loreItems: List<String> = magenta.kitConfig.getKit()
                        .getStringList("kits.$kitName.items.${material.name.lowercase()}.meta.lore")
                    val newList: MutableList<Component> = ArrayList()
                    for (loreItem in loreItems) {
                        newList.add(ModernText.miniModernText(loreItem))
                    }
                    meta.lore(newList)
                }
                item.setItemMeta(meta)
                for (enchantment in Enchantment.values()) {
                    if (magenta.kitConfig.getKit()
                            .contains("kits.$kitName.items.${material.name.lowercase()}.enchants.${enchantment.key.key}")
                    ) {
                        item.addEnchantment(
                            enchantment,
                            magenta.kitConfig.getKit()
                                .getInt(
                                    "kits.$kitName.items.${material.name.lowercase()}.enchants.${
                                        enchantment.key.key
                                    }"
                                )
                        )
                    }
                }
                inv.addItem(item)
                player.updateInventory()
            }
        }
    }

    fun createKit(kitName: String) {
        magenta.kitConfig.getKit().getConfigurationSection("kits.$kitName")?.set("", "")
    }
}