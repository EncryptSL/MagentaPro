package com.github.encryptsl.magenta.api.menu.provider.models.components

import com.github.encryptsl.kmono.lib.extensions.playSound
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.provider.buttons.MenuButton
import com.github.encryptsl.magenta.api.menu.provider.buttons.MenuCloseButton
import com.github.encryptsl.magenta.api.menu.provider.templates.MenuExtender
import dev.triumphteam.gui.builder.item.ItemBuilder
import dev.triumphteam.gui.guis.BaseGui
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.HumanEntity

open class CloseButton(private val magenta: Magenta) : MenuCloseButton, MenuButton {

    enum class BUTTON_ACTION { CLOSE, BACK }

    override fun closeMenu(gui: BaseGui, player: HumanEntity) {
        gui.close(player)
    }

    override fun closeMenuOrBack(
        player: HumanEntity,
        material: Material,
        gui: BaseGui,
        fileConfiguration: FileConfiguration,
        menuExtender: MenuExtender?,
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

        val item = ItemBuilder.from(magenta.itemFactory.shopItem(material, fileConfiguration.getString("menu.gui.button.close.name").toString())).asGuiItem()

        item.setAction {
            val action = BUTTON_ACTION.valueOf(fileConfiguration.getString("menu.gui.button.close.action") ?: BUTTON_ACTION.BACK.name)
            when(action) {
                BUTTON_ACTION.CLOSE ->  { closeMenu(gui, player) }
                BUTTON_ACTION.BACK -> { menuExtender?.openMenu(player) }
            }
        }
        gui.setItem(row, col, item)
    }

    override fun clickSound(humanEntity: HumanEntity, fileConfiguration: FileConfiguration) {
        val type = fileConfiguration.getString("menu.gui.click-sounds.ui", "ui.button.click").toString()
        playSound(humanEntity, type, 5f, 1f)
    }
}