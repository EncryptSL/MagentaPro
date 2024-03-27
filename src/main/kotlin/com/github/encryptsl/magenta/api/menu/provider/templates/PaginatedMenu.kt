package com.github.encryptsl.magenta.api.menu.provider.templates

import dev.triumphteam.gui.guis.PaginatedGui
import net.kyori.adventure.text.Component

interface PaginatedMenu {
    fun paginatedGui(title: Component, size: Int): PaginatedGui
}