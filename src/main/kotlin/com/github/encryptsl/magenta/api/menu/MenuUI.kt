package com.github.encryptsl.magenta.api.menu

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.components.GuiFiller
import com.github.encryptsl.magenta.api.menu.components.template.Menu
import dev.triumphteam.gui.container.GuiContainer
import dev.triumphteam.gui.paper.builder.item.ItemBuilder
import dev.triumphteam.gui.slot.Slot
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class MenuUI(private val magenta: Magenta) {

    private enum class BUTTON_ACTION { CLOSE, BACK }

    fun navigation() {

    }

    fun closeMenuOrBack(
        player: Player,
        material: Material,
        container: GuiContainer<Player, ItemStack>,
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
            .asGuiItem { whoClicked, _ ->
                val action = BUTTON_ACTION.valueOf(fileConfiguration.getString("menu.gui.button.close.action") ?: BUTTON_ACTION.BACK.name)
                when(action) {
                    BUTTON_ACTION.CLOSE ->  { whoClicked.closeInventory() }
                    BUTTON_ACTION.BACK -> { menu?.open(whoClicked) }
                }
            }

        container.set(Slot(row, col), item)
    }

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
            val guiItem = ItemBuilder.from(magenta.itemFactory.item(material, itemName, lore, glowing)).asGuiItem { _, _ -> }

            container.set(slot, guiItem)
        }
    }
}