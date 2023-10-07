package com.github.encryptsl.magenta.common.utils

import com.github.encryptsl.magenta.Magenta
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class TeamIntegration(private val magenta: Magenta) {

    fun createTeams() {
        Teams.createTeam("AFK", "<gray>AFK</gray> ")
    }

    fun setAfk(player: Player) {
        if (!Teams.haveTeam(player, "AFK")) {
            Bukkit.broadcast(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.player.is.afk"), Placeholder.parsed("player", player.name)))
            Teams.addTeam(player, "AFK")
        }
    }

}