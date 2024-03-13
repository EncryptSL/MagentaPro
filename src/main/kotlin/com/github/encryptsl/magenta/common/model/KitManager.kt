package com.github.encryptsl.magenta.common.model

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.ItemBuilder
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Material
import org.bukkit.Registry
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory


class KitManager(private val magenta: Magenta) {

    fun giveKit(player: Player, kitName: String) {
        val inv: Inventory = player.inventory

        if (!magenta.kitConfig.getConfig().contains("kits.$kitName"))
            throw KitNotFoundException(magenta.localeConfig.getMessage("magenta.command.kit.error.not.exist"))

        for (material in Material.entries) {
            if (!magenta.kitConfig.getConfig().contains("kits.$kitName.items.${material.name.lowercase()}")) continue

            val amount: Int = magenta.kitConfig.getConfig().getInt(("kits.$kitName.items.${material.name.lowercase()}.amount"))
            val item = ItemBuilder(material, amount)
            item.setName(ModernText.miniModernText(magenta.kitConfig.getConfig().getString("kits.$kitName.items.${material.name.lowercase()}.meta.displayName", material.name).toString()))

            if (magenta.kitConfig.getConfig().contains("kits.$kitName.items.${material.name.lowercase()}.meta.lore")) {
                val loreItems: List<String> = magenta.kitConfig.getConfig()
                    .getStringList("kits.$kitName.items.${material.name.lowercase()}.meta.lore")

                val lore = loreItems.map { ModernText.miniModernText(it) }.toMutableList()
                item.addLore(lore)
            }

            applyEnchantment(kitName, item, material)
            inv.addItem(item.create())
        }
    }

    fun createKit(player: Player, kitName: String, delay: Int) {
        if (magenta.kitConfig.getConfig().contains("kits.$kitName"))
            throw KitNotFoundException(magenta.localeConfig.getMessage("magenta.command.kit.error.exist"))

        val kitSection = magenta.kitConfig.getConfig().createSection("kits.$kitName")
        kitSection.set("name", kitName)
        kitSection.set("delay", delay)
        for (item in player.inventory) {
            if (item == null) continue
            if (!item.enchantments.isNotEmpty()) continue

            val itemMeta = item.itemMeta
            val itemType = item.type
            kitSection.set("items.${itemType.name.lowercase()}.amount", item.amount)
            for (enchant in item.enchantments) {
                kitSection.set("items.${itemType.name.lowercase()}.enchants.${enchant.key.key().value()}", enchant.value)
            }

            if (item.hasItemMeta()) {
                kitSection.set("items.${itemType.name.lowercase()}.meta.displayName",
                    itemMeta.displayName()?.let { PlainTextComponentSerializer.plainText().serialize(it) })
                item.lore()?.let { lores ->
                    for (lore in lores) {
                        kitSection.set(
                            "items.${itemType.name.lowercase()}.meta.lore",
                            PlainTextComponentSerializer.plainText().serialize(lore)
                        )
                    }
                }
            }
        }
        magenta.kitConfig.save()
    }

    fun deleteKit(kitName: String) {
        if (!magenta.kitConfig.getConfig().contains("kits.$kitName"))
            throw KitNotFoundException(magenta.localeConfig.getMessage("magenta.command.kit.error.not.exist"))

        magenta.kitConfig.set("kits.$kitName", null)
    }

    fun listOfItems(commandSender: CommandSender, kitName: String) {
        for (material in Material.entries) {
            if (magenta.kitConfig.getConfig().contains("kits.$kitName.items.${material.name.lowercase()}")) {
                val amount: Int = magenta.kitConfig.getConfig().getInt(("kits.$kitName.items.${material.name.lowercase()}.amount"))
                val item = ItemBuilder(material, amount)
                if (magenta.kitConfig.getConfig().contains("kits.$kitName.items.${material.name.lowercase()}.meta.displayName")) {
                    item.setName(
                        ModernText.miniModernText(
                            magenta.kitConfig.getConfig()
                                .getString("kits.$kitName.items.${material.name.lowercase()}.meta.displayName").toString()
                        )
                    )
                }
                if (magenta.kitConfig.getConfig().contains("kits.$kitName.items.${material.name.lowercase()}.meta.lore")) {
                    val loreItems: List<String> = magenta.kitConfig.getConfig()
                        .getStringList("kits.$kitName.items.${material.name.lowercase()}.meta.lore")

                    val lore = loreItems.map { ModernText.miniModernText(it) }.toMutableList()
                    item.addLore(lore)
                }

                applyEnchantment(kitName, item, material)

                val itemStack = item.create()
                commandSender.sendMessage(ModernText.miniModernText("<hover:show_text:'<meta>'><green><items></hover>", TagResolver.resolver(
                    Placeholder.parsed("items", itemStack.type.name),
                    Placeholder.component("meta", itemStack.displayName())
                )))
            }
        }
    }

    private fun applyEnchantment(kitName: String, item: ItemBuilder, material: Material) {
        val iterator: Iterator<Enchantment> = Registry.ENCHANTMENT.iterator()
        while(iterator.hasNext()) {
            val enchant = "kits.$kitName.items.${material.name.lowercase()}.enchants.${iterator.next().key.key().value()}"
            if (magenta.kitConfig.getConfig().contains(enchant)) {
                item.addEnchantment(
                    iterator.next(),
                    magenta.kitConfig.getConfig().getInt(enchant)
                )
            }
        }
    }

    class KitNotFoundException(message: String) : Exception(message)
}