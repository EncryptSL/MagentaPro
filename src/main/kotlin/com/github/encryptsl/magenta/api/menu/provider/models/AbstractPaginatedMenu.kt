package com.github.encryptsl.magenta.api.menu.provider.models

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.modules.shop.helpers.ShopHelper
import com.github.encryptsl.magenta.api.menu.provider.buttons.ButtonClickSound
import com.github.encryptsl.magenta.api.menu.provider.models.components.PaginationNavigation
import com.github.encryptsl.magenta.api.menu.provider.templates.PaginatedMenu
import dev.triumphteam.gui.guis.Gui
import dev.triumphteam.gui.guis.PaginatedGui
import net.kyori.adventure.text.Component
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.HumanEntity

abstract class AbstractPaginatedMenu(magenta: Magenta) : PaginationNavigation(magenta), PaginatedMenu, ButtonClickSound {
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

    override fun clickSound(humanEntity: HumanEntity, fileConfiguration: FileConfiguration) {
        val type = fileConfiguration.getString("menu.gui.click-sounds.ui", "ui.button.click").toString()
        ShopHelper.playSound(humanEntity, type, 5f, 1f)
    }
}