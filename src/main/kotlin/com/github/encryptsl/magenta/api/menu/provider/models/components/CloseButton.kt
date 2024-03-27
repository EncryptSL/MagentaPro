package com.github.encryptsl.magenta.api.menu.provider.models.components

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.provider.buttons.MenuCloseButton
import com.github.encryptsl.magenta.api.menu.provider.templates.MenuExtender
import dev.triumphteam.gui.builder.item.ItemBuilder
import dev.triumphteam.gui.guis.BaseGui
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.HumanEntity

open class CloseButton(private val magenta: Magenta) : MenuCloseButton {
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
                        }
                )
            }
        }
    }
}