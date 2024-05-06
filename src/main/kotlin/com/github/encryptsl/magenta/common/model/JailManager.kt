package com.github.encryptsl.magenta.common.model

import com.github.encryptsl.magenta.Magenta
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Location
import org.bukkit.entity.Player


class JailManager(private val magenta: Magenta) {
    fun getJailLocation(jailName: String): Location {
        return magenta.jailConfig.getConfig().getLocation("jails.$jailName.location")
            ?: throw Exception("Something is bad with saved location...")
    }

    fun teleport(player: Player, jailName: String) {
        try {
            val location = magenta.jailManager.getJailLocation(jailName)
            player.teleport(location)
        } catch (e : Exception) {
            player.sendMessage(magenta.locale.translation("magenta.exception", Placeholder.parsed("exception", e.message ?: e.localizedMessage)))
            magenta.logger.severe(e.message ?: e.localizedMessage)
        }
    }
}