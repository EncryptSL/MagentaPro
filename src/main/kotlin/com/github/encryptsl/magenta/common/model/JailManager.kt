package com.github.encryptsl.magenta.common.model

import org.bukkit.Location
import org.bukkit.configuration.file.FileConfiguration


class JailManager(private val fileConfiguration: FileConfiguration) {
    fun getJailLocation(jailName: String): Location? {
        return fileConfiguration.getLocation("jails.$jailName.location")
    }
}