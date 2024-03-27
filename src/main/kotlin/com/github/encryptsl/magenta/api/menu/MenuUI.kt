package com.github.encryptsl.magenta.api.menu

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.shop.helpers.ShopHelper
import com.github.encryptsl.magenta.common.utils.ModernText
import dev.triumphteam.gui.builder.item.ItemBuilder
import dev.triumphteam.gui.components.GuiType
import dev.triumphteam.gui.components.util.GuiFiller
import dev.triumphteam.gui.guis.BaseGui
import dev.triumphteam.gui.guis.Gui
import dev.triumphteam.gui.guis.GuiItem
import dev.triumphteam.gui.guis.PaginatedGui
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.HumanEntity

class MenuUI(private val magenta: Magenta) : Menu {

    override fun simpleGui(title: String, size: Int, guiType: GuiType): Gui {
        return Gui.gui()
            .title(ModernText.miniModernText(title))
            .type(guiType)
            .rows(size)
            .disableItemPlace()
            .disableItemTake()
            .disableItemDrop()
            .disableItemSwap()
            .create()
    }

    override fun simpleGui(title: Component, size: Int, guiType: GuiType): Gui {
        return Gui.gui()
            .title(title)
            .type(guiType)
            .rows(size)
            .disableItemPlace()
            .disableItemTake()
            .disableItemDrop()
            .disableItemSwap()
            .create()
    }

    override fun paginatedGui(title: Component, size: Int): PaginatedGui {
        return Gui.paginated()
            .title(title)
            .rows(size)
            .disableItemPlace()
            .disableItemTake()
            .disableItemDrop()
            .disableItemSwap()
            .create()
    }

    override fun fillBorder(
        guiFiller: GuiFiller,
        fileConfiguration: FileConfiguration
    ) {
        if (fileConfiguration.contains("menu.gui.fill.border")) {
            guiFiller.fillBorder(
                GuiItem(
                    Material.valueOf(
                        fileConfiguration.getString("menu.gui.fill.border").toString()
                    )
                )
            )
            return
        }
    }

    override fun fillTop(
        guiFiller: GuiFiller,
        fileConfiguration: FileConfiguration
    ) {
        if (fileConfiguration.contains("menu.gui.fill.top")) {
            guiFiller.fillTop(
                GuiItem(
                    Material.valueOf(
                        fileConfiguration.getString("menu.gui.fill.top").toString()
                    )
                )
            )
            return
        }
    }

    override fun fillBottom(
        guiFiller: GuiFiller,
        fileConfiguration: FileConfiguration
    ) {
        if (fileConfiguration.contains("menu.gui.fill.bottom")) {
            guiFiller.fillBottom(
                GuiItem(
                    Material.valueOf(
                        fileConfiguration.getString("menu.gui.fill.bottom").toString()
                    )
                )
            )
            return
        }
    }

    override fun fillFull(
        guiFiller: GuiFiller,
        fileConfiguration: FileConfiguration
    ) {
        if (fileConfiguration.contains("menu.gui.fill.full")) {
            guiFiller.fill(
                GuiItem(
                    Material.valueOf(
                        fileConfiguration.getString("menu.gui.fill.full").toString()
                    )
                )
            )
            return
        }
    }

    override fun fillSide(
        guiFiller: GuiFiller,
        fileConfiguration: FileConfiguration
    ) {
        if (fileConfiguration.contains("menu.gui.fill.side")) {
            val side = fileConfiguration.get("menu.gui.fill.mode") ?: GuiFiller.Side.BOTH

            val type = GuiFiller.Side.valueOf(side.toString())

            guiFiller.fillSide(type, listOf(
                GuiItem(
                    Material.valueOf(
                        fileConfiguration.getString("menu.gui.fill.side").toString()
                    )
                ))
            )
            return
        }
    }

    override fun playClickSound(player: HumanEntity, fileConfiguration: FileConfiguration) {
        val type = fileConfiguration.getString("menu.gui.click-sounds.ui", "ui.button.click").toString()
        ShopHelper.playSound(player, type, 5f, 1f)
    }

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
                            gui.previous()
                            playClickSound(player, fileConfiguration)
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
        if (gui.pagesNum < 1) return
        if (fileConfiguration.contains("menu.gui.button.$btnType")) {
            if (!fileConfiguration.contains("menu.gui.button.$btnType.positions"))
                return player.sendMessage(magenta.localeConfig.translation("magenta.menu.error.button.missing.positions", Placeholder.parsed("file", fileConfiguration.name)))

            if (!fileConfiguration.contains("menu.gui.button.$btnType.positions.row"))
                return player.sendMessage(magenta.localeConfig.translation("magenta.menu.error.button.missing.positions.row", Placeholder.parsed("file", fileConfiguration.name)))

            if (!fileConfiguration.contains("menu.gui.button.$btnType.positions.col"))
                return player.sendMessage(magenta.localeConfig.translation("magenta.menu.error.button.missing.positions.col", Placeholder.parsed("file", fileConfiguration.name)))

            if (fileConfiguration.getString("menu.gui.button.$btnType.item").equals(material.name, true)) {
                gui.setItem(fileConfiguration.getInt("menu.gui.button.$btnType.positions.row"),
                    fileConfiguration.getInt("menu.gui.button.$btnType.positions.col"),
                    ItemBuilder.from(magenta.itemFactory.shopItem(material, fileConfiguration.getString("menu.gui.button.$btnType.name").toString().replace("<next_page>", "${gui.nextPageNum}")))
                        .asGuiItem {
                            gui.next()
                            playClickSound(player, fileConfiguration)
                        }
                )
            }
        }
    }

    override fun closeButton(
        player: HumanEntity,
        material: Material,
        gui: BaseGui,
        fileConfiguration: FileConfiguration,
        menuExtender: MenuExtender?,
    ) {
        if (fileConfiguration.contains("menu.gui.button.close")) {
            if (!fileConfiguration.contains("menu.gui.button.close.positions"))
                return player.sendMessage(magenta.localeConfig.translation("magenta.menu.error.button.missing.positions", Placeholder.parsed("file", fileConfiguration.name)))

            if (!fileConfiguration.contains("menu.gui.button.close.positions.row"))
                return player.sendMessage(magenta.localeConfig.translation("magenta.menu.error.button.missing.positions.row", Placeholder.parsed("file", fileConfiguration.name)))

            if (!fileConfiguration.contains("menu.gui.button.close.positions.col"))
                return player.sendMessage(magenta.localeConfig.translation("magenta.menu.error.button.missing.positions.col", Placeholder.parsed("file", fileConfiguration.name)))

            if (fileConfiguration.getString("menu.gui.button.close.item").equals(material.name, true)) {
                gui.setItem(fileConfiguration.getInt("menu.gui.button.close.positions.row"),
                    fileConfiguration.getInt("menu.gui.button.close.positions.col"),
                    ItemBuilder.from(magenta.itemFactory.shopItem(material, fileConfiguration.getString("menu.gui.button.close.name").toString()))
                        .asGuiItem {
                            if (fileConfiguration.getString("menu.gui.button.close.action")?.contains("back") == true) {
                                menuExtender?.openMenu(player)
                            } else {
                                gui.close(player)
                            }
                            playClickSound(player, fileConfiguration)
                        }
                )
            }
        }
    }

    fun useAllFillers(filler: GuiFiller, fileConfiguration: FileConfiguration) {
        if (fileConfiguration.contains("menu.gui.fill")) {
            fillBorder(filler, fileConfiguration)
            fillTop(filler, fileConfiguration)
            fillBottom(filler, fileConfiguration)
            fillSide(filler, fileConfiguration)
            fillFull(filler, fileConfiguration)
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