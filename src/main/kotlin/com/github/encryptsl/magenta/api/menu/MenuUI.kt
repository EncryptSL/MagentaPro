package com.github.encryptsl.magenta.api.menu

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.components.GuiFiller
import dev.triumphteam.gui.container.GuiContainer
import dev.triumphteam.gui.paper.builder.item.ItemBuilder
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class MenuUI(private val magenta: Magenta) {

    fun useAllFillers(rows: Int, container: GuiContainer<Player, ItemStack>, config: FileConfiguration) {
        val menuFiller = GuiFiller(rows, container, config)
        if (config.contains("menu.gui.fill")) {
            menuFiller.fillBorder()
            menuFiller.fillTop()
            //menuFiller.fillBottom()
        }
    }

    fun customItems(player: Player, type: String, fileConfiguration: FileConfiguration, container: GuiContainer<Player, ItemStack>) {
        for (item in fileConfiguration.getConfigurationSection("menu.custom-items")?.getKeys(false)!!) {
            val material = Material.getMaterial(fileConfiguration.getString("menu.custom-items.$item.icon").toString()) ?: continue

            if (!fileConfiguration.contains("menu.custom-items.$item.name"))
                return player.sendMessage(magenta.locale.translation("magenta.menu.error.not.defined.name",
                    Placeholder.parsed("category", type)
                ))
            if (!fileConfiguration.contains("menu.custom-items.$item.position.slot"))
                return player.sendMessage(magenta.locale.translation("magenta.menu.error.not.defined.slot",
                    Placeholder.parsed("category", type)
                ))
            val itemName = fileConfiguration.getString("menu.custom-items.$item.name").toString()
            val slot = fileConfiguration.getInt("menu.custom-items.$item.position.slot")
            val glowing = fileConfiguration.getBoolean("menu.custom-items.$item.options.glowing")
            val lore = fileConfiguration.getStringList("menu.custom-items.$item.lore")
            val guiItem = ItemBuilder.from(magenta.itemFactory.item(material, itemName, lore, glowing)).asGuiItem { p, context -> }

            container.set(slot, guiItem)
        }
    }
}