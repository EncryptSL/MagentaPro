package com.github.encryptsl.magenta.common.utils

import com.github.encryptsl.magenta.Magenta
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class TeamIntegration(private val magenta: Magenta) {

    fun createTeams() {
        Teams.createTeam("OWNER", "<red><bold>OWNER</bold></red> ")
        Teams.createTeam("MODERATOR", "<blue><bold>MODERATOR</bold></blue> ")
        Teams.createTeam("VIP", "<yellow><bold>VIP</bold></yellow> ")
        Teams.createTeam("PLAYER", "<gray>(</gray><green>HRAC</green><gray>)</gray> ")
        Teams.createTeam("AFK", "<gray>AFK</gray> ")
    }

    fun setTeam(player: Player) {
        if (player.hasPermission("magenta.tag.owner")) {
            Teams.addTeam(player, "OWNER")
        } else if (player.hasPermission("magenta.tag.moderator")) {
            Teams.addTeam(player, "MODERATOR")
        } else if (player.hasPermission("magenta.tag.vip")) {
            Teams.addTeam(player, "VIP")
        } else if (player.hasPermission("magenta.tag.player")) {
            Teams.addTeam(player, "PLAYER")
        }
    }

    fun setAfk(player: Player) {
        if (!Teams.haveTeam(player, "AFK")) {
            Bukkit.broadcast(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.player.is.afk"), Placeholder.parsed("player", player.name)))
            Teams.addTeam(player, "AFK")
        }
    }

}