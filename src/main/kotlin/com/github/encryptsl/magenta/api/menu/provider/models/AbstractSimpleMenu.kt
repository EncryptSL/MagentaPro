package com.github.encryptsl.magenta.api.menu.provider.models

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.provider.models.components.CloseButton
import com.github.encryptsl.magenta.api.menu.provider.templates.SimpleMenu
import com.github.encryptsl.magenta.common.utils.ModernText
import dev.triumphteam.gui.components.GuiType
import dev.triumphteam.gui.guis.Gui
import net.kyori.adventure.text.Component

abstract class AbstractSimpleMenu(magenta: Magenta) : CloseButton(magenta), SimpleMenu {

    override fun simpleGui(title: String, size: Int, guiType: GuiType): Gui {
        return Gui.gui()
            .title(ModernText.miniModernText(title))
            .type(guiType)
            .rows(size)
            .disableItemPlace()
            .disableItemTake()
            .disableItemDrop()
            .disableItemSwap()
            .create()
    }

    override fun simpleGui(title: Component, size: Int, guiType: GuiType): Gui {
        return Gui.gui()
            .title(title)
            .type(guiType)
            .rows(size)
            .disableItemPlace()
            .disableItemTake()
            .disableItemDrop()
            .disableItemSwap()
            .create()
    }
}