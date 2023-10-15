package com.github.encryptsl.magenta.api.shop

import com.github.encryptsl.magenta.common.utils.ModernText
import dev.triumphteam.gui.components.GuiType
import dev.triumphteam.gui.guis.Gui
import dev.triumphteam.gui.guis.PaginatedGui
import net.kyori.adventure.text.Component

object ShopBuilder {

    fun gui(title: String, size: Int, guiType: GuiType): Gui
    {
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

    fun guiPaginated(title: Component, size: Int): PaginatedGui
    {
        return Gui.paginated()
            .title(title)
            .rows(size)
            .disableItemPlace()
            .disableItemTake()
            .disableItemDrop()
            .disableItemSwap()
            .create()
    }

}