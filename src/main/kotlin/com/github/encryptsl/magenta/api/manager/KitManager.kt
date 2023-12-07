package com.github.encryptsl.magenta.api.manager

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.ItemBuilder
import com.github.encryptsl.magenta.api.exceptions.KitNotFoundException
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

class KitManager(private val magenta: Magenta) {

    fun giveKit(player: Player, kitName: String) {
        val inv: Inventory = player.inventory

        if (!magenta.kitConfig.getKit().contains("kits.$kitName"))
            throw KitNotFoundException(magenta.localeConfig.getMessage("magenta.command.kit.error.not.exist"))

        for (material in Material.entries) {
            if (magenta.kitConfig.getKit().contains("kits.$kitName.items.${material.name.lowercase()}")) {
                val amount: Int = magenta.kitConfig.getKit().getInt(("kits.$kitName.items.${material.name.lowercase()}.amount"))
                val item = ItemBuilder(material, amount)
                if (magenta.kitConfig.getKit().contains("kits.$kitName.items.${material.name.lowercase()}.meta.displayName")) {
                    item.setName(
                        ModernText.miniModernText(
                            magenta.kitConfig.getKit()
                                .getString("kits.$kitName.items.${material.name.lowercase()}.meta.displayName").toString()
                        )
                    )
                }
                if (magenta.kitConfig.getKit().contains("kits.$kitName.items.${material.name.lowercase()}.meta.lore")) {
                    val loreItems: List<String> = magenta.kitConfig.getKit()
                        .getStringList("kits.$kitName.items.${material.name.lowercase()}.meta.lore")

                    val lore = loreItems.map { ModernText.miniModernText(it) }.toMutableList()
                    item.addLore(lore)
                }

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

                inv.addItem(item.create())
                player.updateInventory()
            }
        }
    }

    fun createKit(player: Player, kitName: String, delay: Int) {
        if (magenta.kitConfig.getKit().contains("kits.$kitName"))
            throw KitNotFoundException(magenta.localeConfig.getMessage("magenta.command.kit.error.exist"))

        val kitSection = magenta.kitConfig.getKit().createSection("kits.$kitName")
        kitSection.set("name", kitName)
        kitSection.set("delay", delay)
        player.inventory.forEach { item ->
            if (item != null) {
                val itemMeta = item.itemMeta
                val itemType = item.type
                kitSection.set("items.${itemType.name.lowercase()}.amount", item.amount)
                println(itemType.name)
                if (item.enchantments.isNotEmpty()) {
                    item.enchantments.forEach { enchant ->
                        kitSection.set("items.${itemType.name.lowercase()}.enchants.${enchant.key.key().value()}", enchant.value)
                    }
                }
                if (item.hasItemMeta()) {
                    kitSection.set("items.${itemType.name.lowercase()}.meta.displayName",
                        itemMeta.displayName()?.let { PlainTextComponentSerializer.plainText().serialize(it) })
                    item.lore()?.forEach { a ->
                        kitSection.set("items.${itemType.name.lowercase()}.meta.lore", PlainTextComponentSerializer.plainText().serialize(a))
                    }
                }
            }
        }
        magenta.kitConfig.save()
    }

    fun deleteKit(kitName: String) {
        if (!magenta.kitConfig.getKit().contains("kits.$kitName"))
            throw KitNotFoundException(magenta.localeConfig.getMessage("magenta.command.kit.error.not.exist"))

        magenta.kitConfig.getKit().set("kits.$kitName", null)
        magenta.kitConfig.save()
    }

    fun listOfItems(commandSender: CommandSender, kitName: String) {
        for (material in Material.entries) {
            if (magenta.kitConfig.getKit().contains("kits.$kitName.items.${material.name.lowercase()}")) {
                val amount: Int = magenta.kitConfig.getKit().getInt(("kits.$kitName.items.${material.name.lowercase()}.amount"))
                val item = ItemBuilder(material, amount)
                if (magenta.kitConfig.getKit().contains("kits.$kitName.items.${material.name.lowercase()}.meta.displayName")) {
                    item.setName(
                        ModernText.miniModernText(
                            magenta.kitConfig.getKit()
                                .getString("kits.$kitName.items.${material.name.lowercase()}.meta.displayName").toString()
                        )
                    )
                }
                if (magenta.kitConfig.getKit().contains("kits.$kitName.items.${material.name.lowercase()}.meta.lore")) {
                    val loreItems: List<String> = magenta.kitConfig.getKit()
                        .getStringList("kits.$kitName.items.${material.name.lowercase()}.meta.lore")

                    val lore = loreItems.map { ModernText.miniModernText(it) }.toMutableList()
                    item.addLore(lore)
                }
                for (enchantment in Enchantment.values()) {
                    val enchant = "kits.$kitName.items.${material.name.lowercase()}.enchants.${enchantment.key().value()}"
                    if (magenta.kitConfig.getKit().contains(enchant)) {
                        item.addEnchantment(
                            enchantment,
                            magenta.kitConfig.getKit().getInt(enchant)
                        )
                    }
                }
                val itemStack = item.create()
                commandSender.sendMessage(ModernText.miniModernText("<hover:show_text:'<meta>'><green><items></hover>", TagResolver.resolver(
                    Placeholder.parsed("items", itemStack.type.name),
                    Placeholder.component("meta", itemStack.displayName())
                )))
            }
        }
    }
}