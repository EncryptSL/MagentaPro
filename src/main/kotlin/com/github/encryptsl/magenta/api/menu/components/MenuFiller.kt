package com.github.encryptsl.magenta.api.menu.components

import dev.triumphteam.gui.components.util.GuiFiller
import dev.triumphteam.gui.guis.GuiItem
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration


class MenuFiller(
    private val guiFiller: GuiFiller,
    private val config: FileConfiguration
) {
    fun fillBorder() {
        if (config.contains("menu.gui.fill.border")) {
            guiFiller.fillBorder(
                GuiItem(
                    Material.valueOf(
                        config.getString("menu.gui.fill.border").toString()
                    )
                )
            )
            return
        }
    }

    fun fillTop() {
        if (config.contains("menu.gui.fill.top")) {
            guiFiller.fillTop(
                GuiItem(
                    Material.valueOf(
                        config.getString("menu.gui.fill.top").toString()
                    )
                )
            )
            return
        }
    }

    fun fillBottom() {
        if (config.contains("menu.gui.fill.bottom")) {
            guiFiller.fillBottom(
                GuiItem(
                    Material.valueOf(
                        config.getString("menu.gui.fill.bottom").toString()
                    )
                )
            )
            return
        }
    }

    fun fillFull() {
        if (config.contains("menu.gui.fill.full")) {
            guiFiller.fill(
                GuiItem(
                    Material.valueOf(
                        config.getString("menu.gui.fill.full").toString()
                    )
                )
            )
            return
        }
    }

    fun fillSide() {
        if (config.contains("menu.gui.fill.side")) {
            val side = config.get("menu.gui.fill.mode") ?: GuiFiller.Side.BOTH

            val type = GuiFiller.Side.valueOf(side.toString())

            guiFiller.fillSide(type, listOf(
                GuiItem(
                    Material.valueOf(
                        config.getString("menu.gui.fill.side").toString()
                    )
                )
            ))
            return
        }
    }
}