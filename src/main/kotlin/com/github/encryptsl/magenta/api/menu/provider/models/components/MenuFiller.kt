package com.github.encryptsl.magenta.api.menu.provider.models.components

import com.github.encryptsl.magenta.api.menu.provider.templates.MenuElementsFiller
import dev.triumphteam.gui.components.util.GuiFiller
import dev.triumphteam.gui.guis.GuiItem
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration

class MenuFiller : MenuElementsFiller {
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
                )
            )
            )
            return
        }
    }
}