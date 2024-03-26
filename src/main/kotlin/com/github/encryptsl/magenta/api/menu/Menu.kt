package com.github.encryptsl.magenta.api.menu

import dev.triumphteam.gui.components.GuiType
import dev.triumphteam.gui.components.util.GuiFiller
import dev.triumphteam.gui.guis.BaseGui
import dev.triumphteam.gui.guis.Gui
import dev.triumphteam.gui.guis.PaginatedGui
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.HumanEntity

interface Menu {

    fun simpleGui(title: String, size: Int, guiType: GuiType): Gui
    fun simpleGui(title: Component, size: Int, guiType: GuiType): Gui

    fun paginatedGui(title: Component, size: Int): PaginatedGui

    fun fillBorder(guiFiller: GuiFiller, fileConfiguration: FileConfiguration)
    fun fillTop(guiFiller: GuiFiller, fileConfiguration: FileConfiguration)
    fun fillBottom(guiFiller: GuiFiller, fileConfiguration: FileConfiguration)

    fun fillFull(guiFiller: GuiFiller, fileConfiguration: FileConfiguration)

    fun fillSide(guiFiller: GuiFiller, fileConfiguration: FileConfiguration)

    fun playClickSound(player: HumanEntity, fileConfiguration: FileConfiguration)

    fun previousPage(
        player: HumanEntity,
        material: Material,
        fileConfiguration: FileConfiguration,
        btnType: String,
        gui: PaginatedGui
    )

    fun nextPage(
        player: HumanEntity,
        material: Material,
        fileConfiguration: FileConfiguration,
        btnType: String,
        gui: PaginatedGui
    )

    fun closeButton(
        player: HumanEntity,
        material: Material,
        gui: BaseGui,
        fileConfiguration: FileConfiguration,
        menuExtender: MenuExtender?,
    )
}