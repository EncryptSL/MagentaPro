package com.github.encryptsl.magenta.api.menu.provider.templates

import dev.triumphteam.gui.components.util.GuiFiller
import org.bukkit.configuration.file.FileConfiguration

interface MenuElementsFiller {
    fun fillBorder(guiFiller: GuiFiller, fileConfiguration: FileConfiguration)
    fun fillTop(guiFiller: GuiFiller, fileConfiguration: FileConfiguration)
    fun fillBottom(guiFiller: GuiFiller, fileConfiguration: FileConfiguration)

    fun fillFull(guiFiller: GuiFiller, fileConfiguration: FileConfiguration)

    fun fillSide(guiFiller: GuiFiller, fileConfiguration: FileConfiguration)
}