package com.github.encryptsl.magenta.api.menu.provider.models.components

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.provider.buttons.PaginationNavigation
import dev.triumphteam.gui.builder.item.ItemBuilder
import dev.triumphteam.gui.guis.PaginatedGui
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.HumanEntity

open class PaginationNavigation(private val magenta: Magenta) : CloseButton(magenta), PaginationNavigation {
    override fun previousPage(
        player: HumanEntity,
        material: Material,
        fileConfiguration: FileConfiguration,
        btnType: String,
        gui: PaginatedGui
    ) {
        if (gui.currentPageNum == 1) return
        if (fileConfiguration.contains("menu.gui.button.$btnType")) {
            if (!fileConfiguration.contains("menu.gui.button.$btnType.positions"))
                return player.sendMessage(magenta.localeConfig.translation("magenta.menu.error.button.missing.positions"))

            if (!fileConfiguration.contains("menu.gui.button.$btnType.positions.row"))
                return player.sendMessage(magenta.localeConfig.translation("magenta.menu.error.button.missing.positions.row"))

            if (!fileConfiguration.contains("menu.gui.button.$btnType.positions.col"))
                return player.sendMessage(magenta.localeConfig.translation("magenta.menu.error.button.missing.positions.col"))

            if (fileConfiguration.getString("menu.gui.button.$btnType.item").equals(material.name, true)) {
                gui.setItem(fileConfiguration.getInt("menu.gui.button.$btnType.positions.row"),
                    fileConfiguration.getInt("menu.gui.button.$btnType.positions.col"),
                    ItemBuilder.from(magenta.itemFactory.shopItem(material, fileConfiguration.getString("menu.gui.button.$btnType.name").toString().replace("<prev_page>", "${gui.prevPageNum}")))
                        .asGuiItem {
                            clickSound(it.whoClicked, fileConfiguration)
                            gui.previous()
                        }
                )
            }
        }
    }

    override fun nextPage(
        player: HumanEntity,
        material: Material,
        fileConfiguration: FileConfiguration,
        btnType: String,
        gui: PaginatedGui
    ) {
        if (gui.pagesNum < 1 && 1 == gui.pagesNum) return
        if (fileConfiguration.contains("menu.gui.button.$btnType")) {
            if (!fileConfiguration.contains("menu.gui.button.$btnType.positions"))
                return player.sendMessage(magenta.localeConfig.translation("magenta.menu.error.button.missing.positions",
                    Placeholder.parsed("file", fileConfiguration.name))
                )

            if (!fileConfiguration.contains("menu.gui.button.$btnType.positions.row"))
                return player.sendMessage(magenta.localeConfig.translation("magenta.menu.error.button.missing.positions.row",
                    Placeholder.parsed("file", fileConfiguration.name))
                )

            if (!fileConfiguration.contains("menu.gui.button.$btnType.positions.col"))
                return player.sendMessage(magenta.localeConfig.translation("magenta.menu.error.button.missing.positions.col",
                    Placeholder.parsed("file", fileConfiguration.name))
                )

            if (fileConfiguration.getString("menu.gui.button.$btnType.item").equals(material.name, true)) {
                gui.setItem(fileConfiguration.getInt("menu.gui.button.$btnType.positions.row"),
                    fileConfiguration.getInt("menu.gui.button.$btnType.positions.col"),
                    ItemBuilder.from(magenta.itemFactory.shopItem(material, fileConfiguration.getString("menu.gui.button.$btnType.name").toString().replace("<next_page>", "${gui.nextPageNum}")))
                        .asGuiItem {
                            clickSound(it.whoClicked, fileConfiguration)
                            gui.next()
                        }
                )
            }
        }
    }
}