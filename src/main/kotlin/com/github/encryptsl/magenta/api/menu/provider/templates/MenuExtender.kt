package com.github.encryptsl.magenta.api.menu.provider.templates

import org.bukkit.entity.HumanEntity

interface MenuExtender {
    fun openMenu(player: HumanEntity)
}