package com.github.encryptsl.magenta.api.menu.provider.buttons

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.HumanEntity

interface ButtonClickSound {
    fun clickSound(humanEntity: HumanEntity, fileConfiguration: FileConfiguration)
}