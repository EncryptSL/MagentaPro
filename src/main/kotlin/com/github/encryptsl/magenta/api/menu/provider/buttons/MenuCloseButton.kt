package com.github.encryptsl.magenta.api.menu.provider.buttons

import com.github.encryptsl.magenta.api.menu.provider.templates.MenuExtender
import dev.triumphteam.gui.guis.BaseGui
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.HumanEntity

interface MenuCloseButton {
    fun closeMenuOrBack(
        player: HumanEntity,
        material: Material,
        gui: BaseGui,
        fileConfiguration: FileConfiguration,
        menuExtender: MenuExtender?,
    )
    fun closeMenu(gui: BaseGui, player: HumanEntity)
}