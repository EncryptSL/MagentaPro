package com.github.encryptsl.magenta.common.utils

import org.bukkit.entity.Player

class TeamIntegration {

    fun createTeams() {
        Teams.createTeam("OWNER", "&4&lOWNER ")
        Teams.createTeam("MODERATOR", "&3&lOWNER ")
        Teams.createTeam("VIP", "&e&lVIP ")
        Teams.createTeam("PLAYER", "&7(&aHRAC&7) ")
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