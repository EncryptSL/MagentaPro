package com.github.encryptsl.magenta.api.shop.helpers

import com.github.encryptsl.magenta.api.config.ShopConfig
import com.github.encryptsl.magenta.api.shop.credits.CreditShop
import com.github.encryptsl.magenta.api.shop.vault.VaultShop
import dev.triumphteam.gui.components.GuiType
import dev.triumphteam.gui.components.util.GuiFiller
import dev.triumphteam.gui.guis.Gui
import dev.triumphteam.gui.guis.PaginatedGui
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

interface ShopItems {

    fun simpleGui(title: String, size: Int, guiType: GuiType): Gui

    fun paginatedGui(title: Component, size: Int): PaginatedGui

    fun fillBorder(guiFiller: GuiFiller, fileConfiguration: FileConfiguration)
    fun fillTop(guiFiller: GuiFiller, fileConfiguration: FileConfiguration)
    fun fillBottom(guiFiller: GuiFiller, fileConfiguration: FileConfiguration)

    fun fillFull(guiFiller: GuiFiller, fileConfiguration: FileConfiguration)

    fun fillSide(guiFiller: GuiFiller, fileConfiguration: FileConfiguration)

    fun previousPage(
        player: Player,
        material: Material,
        shopCategory: ShopConfig,
        btnType: String,
        gui: PaginatedGui
    )

    fun nextPage(
        player: Player,
        material: Material,
        shopCategory: ShopConfig,
        btnType: String,
        gui: PaginatedGui
    )

    fun closeButton(
        player: Player,
        material: Material,
        gui: PaginatedGui,
        shopCategory: ShopConfig,
        vaultShop: VaultShop
    )

    fun closeButton(
        player: Player,
        material: Material,
        gui: PaginatedGui,
        shopCategory: ShopConfig,
        creditShop: CreditShop
    )

}