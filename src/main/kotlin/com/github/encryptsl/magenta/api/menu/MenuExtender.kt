package com.github.encryptsl.magenta.api.menu

import org.bukkit.entity.HumanEntity

interface MenuExtender {
    fun openMenu(player: HumanEntity)
}