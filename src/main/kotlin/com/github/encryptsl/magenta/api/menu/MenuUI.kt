package com.github.encryptsl.magenta.api.menu

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.provider.models.AbstractPaginatedMenu
import com.github.encryptsl.magenta.api.menu.provider.models.AbstractSimpleMenu
import com.github.encryptsl.magenta.api.menu.provider.models.components.MenuFiller
import com.github.encryptsl.magenta.api.menu.provider.templates.MenuExtender
import dev.triumphteam.gui.builder.item.ItemBuilder
import dev.triumphteam.gui.components.util.GuiFiller
import dev.triumphteam.gui.guis.BaseGui
import dev.triumphteam.gui.guis.PaginatedGui
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.HumanEntity

class MenuUI(private val magenta: Magenta) {

    inner class PaginationMenu(magenta: Magenta, private val menuExtender: MenuExtender) : AbstractPaginatedMenu(magenta) {
        fun paginatedControlButtons(player: HumanEntity, fileConfiguration: FileConfiguration, gui: PaginatedGui) {
            for (material in Material.entries) {
                previousPage(player, material, fileConfiguration, "previous", gui)
                closeButton(player, material, gui, fileConfiguration, menuExtender)
                previousPage(player, material, fileConfiguration, "next", gui)
            }
        }
    }

    inner class SimpleMenu(magenta: Magenta) : AbstractSimpleMenu(magenta)


    fun useAllFillers(filler: GuiFiller, fileConfiguration: FileConfiguration) {
        val menuFiller = MenuFiller()
        if (fileConfiguration.contains("menu.gui.fill")) {
            menuFiller.fillBorder(filler, fileConfiguration)
            menuFiller.fillTop(filler, fileConfiguration)
            menuFiller.fillBottom(filler, fileConfiguration)
            menuFiller.fillSide(filler, fileConfiguration)
            menuFiller.fillFull(filler, fileConfiguration)
        }
    }

    fun customItems(player: HumanEntity, type: String, fileConfiguration: FileConfiguration, gui: BaseGui) {
        for (item in fileConfiguration.getConfigurationSection("menu.custom-items")?.getKeys(false)!!) {
            val material = Material.getMaterial(fileConfiguration.getString("menu.custom-items.$item.icon").toString())
            if (material != null) {
                if (!fileConfiguration.contains("menu.custom-items.$item.name"))
                    return player.sendMessage(magenta.localeConfig.translation("magenta.menu.error.not.defined.name",
                        Placeholder.parsed("category", type)
                    ))
                if (!fileConfiguration.contains("menu.custom-items.$item.position.slot"))
                    return player.sendMessage(magenta.localeConfig.translation("magenta.menu.error.not.defined.slot",
                        Placeholder.parsed("category", type)
                    ))
                val itemName = fileConfiguration.getString("menu.custom-items.$item.name").toString()
                val slot = fileConfiguration.getInt("menu.custom-items.$item.position.slot")
                val glowing = fileConfiguration.getBoolean("menu.custom-items.$item.options.glowing")
                val lore = fileConfiguration.getStringList("menu.custom-items.$item.lore")
                val guiItem = ItemBuilder.from(magenta.itemFactory.item(material, itemName, lore, glowing)).asGuiItem {
                    return@asGuiItem
                }
                gui.setItem(slot, guiItem)
            }
        }
    }
}