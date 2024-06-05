package com.github.encryptsl.magenta.api.menu.components

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.kmono.lib.utils.ItemCreator
import com.github.encryptsl.magenta.Magenta
import dev.triumphteam.gui.builder.item.ItemBuilder
import dev.triumphteam.gui.guis.PaginatedGui
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

class PaginationPanel(
    private val magenta: Magenta,
    private val gui: PaginatedGui,
    private val config: FileConfiguration
) {

    fun previousPage(player: Player, material: Material) {
        if (gui.currentPageNum == 1) return
        if (config.contains("menu.gui.button.previous")) {
            if (!config.contains("menu.gui.button.previous.positions"))
                return player.sendMessage(magenta.locale.translation("magenta.menu.error.button.missing.positions"))

            if (!config.contains("menu.gui.button.previous.positions.row"))
                return player.sendMessage(magenta.locale.translation("magenta.menu.error.button.missing.positions.row"))

            if (!config.contains("menu.gui.button.previous.positions.col"))
                return player.sendMessage(magenta.locale.translation("magenta.menu.error.button.missing.positions.col"))

            if (config.getString("menu.gui.button.previous.item").equals(material.name, true)) {
                val guiItem = ItemBuilder.from(
                    ItemCreator(material, 1)
                        .setName(ModernText.miniModernText(config.getString("menu.gui.button.previous.name").toString(), Placeholder.parsed("prev_page", gui.prevPageNum.toString()))).create()
                ).asGuiItem {
                    gui.previous()
                }

                gui.setItem(config.getInt("menu.gui.button.previous.positions.row"),
                    config.getInt("menu.gui.button.previous.positions.col"),
                    guiItem
                )
            }
        }
    }

    fun nextPage(player: Player, material: Material) {
        if (gui.pagesNum >= gui.currentPageNum) return
        if (config.contains("menu.gui.button.next")) {
            if (!config.contains("menu.gui.button.next.positions"))
                return player.sendMessage(magenta.locale.translation("magenta.menu.error.button.missing.positions"))

            if (!config.contains("menu.gui.button.next.positions.row"))
                return player.sendMessage(magenta.locale.translation("magenta.menu.error.button.missing.positions.row"))

            if (!config.contains("menu.gui.button.next.positions.col"))
                return player.sendMessage(magenta.locale.translation("magenta.menu.error.button.missing.positions.col"))

            if (config.getString("menu.gui.button.next.item").equals(material.name, true)) {
                val guiItem = ItemBuilder.from(
                    ItemCreator(material, 1)
                        .setName(ModernText.miniModernText(config.getString("menu.gui.button.next.name").toString(), Placeholder.parsed("next_page", gui.nextPageNum.toString()))).create()
                ).asGuiItem {
                    gui.next()
                }
                gui.setItem(config.getInt("menu.gui.button.next.positions.row"),
                    config.getInt("menu.gui.button.next.positions.col"),
                    guiItem
                )
            }
        }
    }

}