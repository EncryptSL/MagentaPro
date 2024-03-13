package com.github.encryptsl.magenta.common.model

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.file.FileConfiguration


class JailManager(private val fileConfiguration: FileConfiguration) {
    fun getJailLocation(jailName: String): Location {
        return Location(Bukkit.getWorld(fileConfiguration.getString("jails.${jailName}.location.world").toString()),
            fileConfiguration.getDouble("jails.${jailName}.location.x"),
            fileConfiguration.getDouble("jails.${jailName}.location.y"),
            fileConfiguration.getDouble("jails.${jailName}.location.z"),
            fileConfiguration.getInt("jails.${jailName}.location.yaw").toFloat(),
            fileConfiguration.getInt("jails.${jailName}.location.pitch").toFloat()
        )
    }

}