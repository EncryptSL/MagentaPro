package com.github.encryptsl.magenta.api.menu

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.kmono.lib.extensions.playSound
import com.github.encryptsl.kmono.lib.utils.ItemCreator
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

    private enum class ButtonAction { CLOSE, BACK }

    fun simpleBuilderGui(rows: Int, title: Component, config: FileConfiguration): Gui {
        val builder = Gui.gui(GuiType.CHEST).rows(rows).title(title).disableAllInteractions().create()

        builder.setDefaultClickAction { context ->
            if (context.currentItem != null && !isGlass(context.currentItem)) {
                if (context.isLeftClick || context.isRightClick) {
                    val type = config.getString("menu.gui.click-sounds.ui", "ui.button.click").toString()
                    playSound(context.whoClicked, type, 5f, 1f)
                }
            }
        }

        return builder
    }

    fun paginatedBuilderGui(rows: Int, title: Component, config: FileConfiguration): PaginatedGui {
        val builder = Gui.paginated().rows(rows).title(title).disableAllInteractions().pageSize(100).create()

        builder.setDefaultClickAction { context ->
            if (context.currentItem != null && !isGlass(context.currentItem)) {
                if (context.isLeftClick || context.isRightClick) {
                    val type = config.getString("menu.gui.click-sounds.ui", "ui.button.click").toString()
                    playSound(context.whoClicked, type, 5f, 1f)
                }
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
        config: FileConfiguration,
        menu: Menu?,
    ) {

        if (!config.contains("menu.gui.button.close")) return

        if (!config.contains("menu.gui.button.close.positions"))
            return player.sendMessage(
                magenta.locale.translation("magenta.menu.error.button.missing.positions",
                    Placeholder.parsed("file", config.name))
            )

        if (!config.contains("menu.gui.button.close.positions.row"))
            return player.sendMessage(magenta.locale.translation("magenta.menu.error.button.missing.positions.row",
                Placeholder.parsed("file", config.name))
            )

        if (!config.contains("menu.gui.button.close.positions.col"))
            return player.sendMessage(magenta.locale.translation("magenta.menu.error.button.missing.positions.col",
                Placeholder.parsed("file", config.name))
            )

        if (!config.getString("menu.gui.button.close.item").equals(material.name, true)) return

        val action = config.getString("menu.gui.button.close.action").toString()
        val row = config.getInt("menu.gui.button.close.positions.row")
        val col = config.getInt("menu.gui.button.close.positions.col")
        val buttonName = config.getString("menu.gui.button.close.name").toString()

        val itemStack = ItemCreator(material, 1)
            .setName(ModernText.miniModernText(buttonName)).create()

        val item = ItemBuilder.from(itemStack).asGuiItem { clickEvent ->
            val buttonAction = ButtonAction.entries.firstOrNull { el -> el.name.equals(action, true) }
            when(buttonAction) {
                ButtonAction.CLOSE ->  { clickEvent.whoClicked.closeInventory() }
                ButtonAction.BACK -> { menu?.open(clickEvent.whoClicked as Player) }
                else -> clickEvent.whoClicked.closeInventory()
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

    fun customItems(player: Player, type: String, config: FileConfiguration, gui: BaseGui) {
        for (item in config.getConfigurationSection("menu.custom-items")?.getKeys(false)!!) {
            val material = Material.getMaterial(config.getString("menu.custom-items.$item.icon").toString()) ?: continue

            if (!config.contains("menu.custom-items.$item.name"))
                return player.sendMessage(magenta.locale.translation("magenta.menu.error.not.defined.name",
                    Placeholder.parsed("category", type)
                ))
            if (!config.contains("menu.custom-items.$item.position.slot"))
                return player.sendMessage(magenta.locale.translation("magenta.menu.error.not.defined.slot",
                    Placeholder.parsed("category", type)
                ))
            val itemName = config.getString("menu.custom-items.$item.name").toString()
            val slot = config.getInt("menu.custom-items.$item.position.slot")
            val glowing = config.getBoolean("menu.custom-items.$item.options.glowing")
            val lore = config.getStringList("menu.custom-items.$item.lore")

            gui.setItem(slot, ItemBuilder.from(
                ItemCreator(material)
                    .setName(ModernText.miniModernText(itemName))
                    .setGlowing(glowing)
                    .addLore(lore.map { ModernText.miniModernText(it) }.toMutableList()).create()
            ).asGuiItem())
        }
    }

    private fun isGlass(itemStack: ItemStack?): Boolean {
        return itemStack?.type?.name?.contains("_GLASS") == true || itemStack?.type?.name?.contains("_GLASS_PANE") == true
    }
}