package com.github.encryptsl.magenta.api.menu

import com.github.encryptsl.kmono.lib.extensions.playSound
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.components.MenuFiller
import com.github.encryptsl.magenta.api.menu.components.PaginationPanel
import com.github.encryptsl.magenta.api.menu.components.template.Menu
import dev.triumphteam.gui.builder.item.ItemBuilder
import dev.triumphteam.gui.components.GuiType
import dev.triumphteam.gui.guis.BaseGui
import dev.triumphteam.gui.guis.Gui
import dev.triumphteam.gui.guis.PaginatedGui
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class MenuUI(private val magenta: Magenta) {

    private enum class BUTTON_ACTION { CLOSE, BACK }

    fun simpleBuilderGui(rows: Int, title: Component, config: FileConfiguration): Gui {
        val builder = Gui.gui(GuiType.CHEST).rows(rows).title(title).disableAllInteractions().create()

        builder.setDefaultClickAction { context ->
            if (context.currentItem != null && context.isLeftClick && context.isRightClick && !isGlass(context.currentItem)) {
                val type = config.getString("menu.gui.click-sounds.ui", "ui.button.click").toString()
                playSound(context.whoClicked, type, 5f, 1f)
            }
        }

        return builder
    }

    fun paginatedBuilderGui(rows: Int, title: Component, config: FileConfiguration): PaginatedGui {
        val builder = Gui.paginated().rows(rows).title(title).disableAllInteractions().pageSize(100).create()

        builder.setDefaultClickAction { context ->
            if (context.currentItem != null && context.isLeftClick && context.isRightClick && !isGlass(context.currentItem)) {
                val type = config.getString("menu.gui.click-sounds.ui", "ui.button.click").toString()
                playSound(context.whoClicked, type, 5f, 1f)
            }
        }

        return builder
    }

    fun pagination(
        player: Player,
        gui: PaginatedGui,
        config: FileConfiguration,
        menu: Menu?
    ) {
        val paginationPanel = paginationPanel(gui, config)

        for (material in Material.entries) {
            paginationPanel.previousPage(player, material)
            closeMenuOrBack(player, material, gui, config, menu)
            paginationPanel.nextPage(player, material)
        }
    }

    private fun paginationPanel(
        gui: PaginatedGui,
        config: FileConfiguration
    ) : PaginationPanel {
        return PaginationPanel(magenta, gui, config)
    }

    fun closeMenuOrBack(
        player: Player,
        material: Material,
        container: BaseGui,
        fileConfiguration: FileConfiguration,
        menu: Menu?,
    ) {

        if (!fileConfiguration.contains("menu.gui.button.close")) return

        if (!fileConfiguration.contains("menu.gui.button.close.positions"))
            return player.sendMessage(magenta.locale.translation("magenta.menu.error.button.missing.positions", Placeholder.parsed("file", fileConfiguration.name)))

        if (!fileConfiguration.contains("menu.gui.button.close.positions.row"))
            return player.sendMessage(magenta.locale.translation("magenta.menu.error.button.missing.positions.row", Placeholder.parsed("file", fileConfiguration.name)))

        if (!fileConfiguration.contains("menu.gui.button.close.positions.col"))
            return player.sendMessage(magenta.locale.translation("magenta.menu.error.button.missing.positions.col", Placeholder.parsed("file", fileConfiguration.name)))

        if (!fileConfiguration.getString("menu.gui.button.close.item").equals(material.name, true)) return

        val row = fileConfiguration.getInt("menu.gui.button.close.positions.row")
        val col = fileConfiguration.getInt("menu.gui.button.close.positions.col")

        val item = ItemBuilder.from(magenta.itemFactory.shopItem(material, fileConfiguration.getString("menu.gui.button.close.name").toString()))
            .asGuiItem { clickEvent ->
                val action = BUTTON_ACTION.valueOf(fileConfiguration.getString("menu.gui.button.close.action") ?: BUTTON_ACTION.BACK.name)
                when(action) {
                    BUTTON_ACTION.CLOSE ->  { clickEvent.whoClicked.closeInventory() }
                    BUTTON_ACTION.BACK -> { menu?.open(clickEvent.whoClicked as Player) }
                }
            }

        container.setItem(row, col, item)
    }

    fun useAllFillers(gui: BaseGui, config: FileConfiguration) {
        val menuFiller = MenuFiller(gui.filler, config)
        if (config.contains("menu.gui.fill")) {
            menuFiller.fillBorder()
            menuFiller.fillTop()
            menuFiller.fillSide()
            menuFiller.fillFull()
            menuFiller.fillBottom()
        }
    }

    fun customItems(player: Player, type: String, fileConfiguration: FileConfiguration, gui: BaseGui) {
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
            val guiItem = ItemBuilder.from(magenta.itemFactory.item(material, itemName, lore, glowing)).asGuiItem()

            gui.setItem(slot, guiItem)
        }
    }

    private fun isGlass(itemStack: ItemStack?): Boolean {
        return itemStack?.type?.name?.contains("_GLASS") == true || itemStack?.type?.name?.contains("_GLASS_PANE") == true
    }
}