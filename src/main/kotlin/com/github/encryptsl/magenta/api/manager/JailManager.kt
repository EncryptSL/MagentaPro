package com.github.encryptsl.magenta.api.manager

import com.github.encryptsl.magenta.api.config.JailConfig
import org.bukkit.Bukkit
import org.bukkit.Location


class JailManager(private val jailConfig: JailConfig) {
    fun getJailLocation(jailName: String): Location {
        return Location(Bukkit.getWorld(jailConfig.getJail().getString("jails.${jailName}.location.world").toString()),
            jailConfig.getJail().getDouble("jails.${jailName}.location.x"),
            jailConfig.getJail().getDouble("jails.${jailName}.location.y"),
            jailConfig.getJail().getDouble("jails.${jailName}.location.z"),
            jailConfig.getJail().getInt("jails.${jailName}.location.yaw").toFloat(),
            jailConfig.getJail().getInt("jails.${jailName}.location.pitch").toFloat()
        )
    }

}