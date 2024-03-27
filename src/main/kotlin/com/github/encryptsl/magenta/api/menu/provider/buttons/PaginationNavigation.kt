package com.github.encryptsl.magenta.api.menu.provider.buttons

import dev.triumphteam.gui.guis.PaginatedGui
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.HumanEntity

interface PaginationNavigation {
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
}