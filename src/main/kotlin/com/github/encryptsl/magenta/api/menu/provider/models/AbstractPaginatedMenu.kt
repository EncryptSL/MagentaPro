package com.github.encryptsl.magenta.api.menu.provider.models

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.provider.models.components.PaginationNavigation
import com.github.encryptsl.magenta.api.menu.provider.templates.PaginatedMenu
import dev.triumphteam.gui.guis.Gui
import dev.triumphteam.gui.guis.PaginatedGui
import net.kyori.adventure.text.Component

abstract class AbstractPaginatedMenu(magenta: Magenta) : PaginationNavigation(magenta), PaginatedMenu {
    override fun paginatedGui(title: Component, size: Int): PaginatedGui {
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