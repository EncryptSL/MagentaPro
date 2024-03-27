package com.github.encryptsl.magenta.api.menu.provider.templates

import dev.triumphteam.gui.components.GuiType
import dev.triumphteam.gui.guis.Gui
import net.kyori.adventure.text.Component

interface SimpleMenu {
    fun simpleGui(title: String, size: Int, guiType: GuiType): Gui
    fun simpleGui(title: Component, size: Int, guiType: GuiType): Gui
}