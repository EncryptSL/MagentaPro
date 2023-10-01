package com.github.encryptsl.magenta.common.utils

import org.bukkit.entity.Player

class TeamIntegration {

    fun createTeams() {
        Teams.createTeam("OWNER", "<red><bold>OWNER</bold></red> ")
        Teams.createTeam("MODERATOR", "<blue><bold>MODERATOR</bold></blue> ")
        Teams.createTeam("VIP", "<yellow><bold>VIP</bold></yellow> ")
        Teams.createTeam("PLAYER", "<gray>(</gray><green>HRAC</green><gray>)</gray> ")
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

}