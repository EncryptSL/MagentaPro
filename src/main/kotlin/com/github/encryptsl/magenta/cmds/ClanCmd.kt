package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandDescription("Provided by plugin MagentaPro")
class ClanCmd {

    @CommandMethod("clan create <name>")
    fun onClanCreate(player: Player, @Argument(value = "name") clan: String) {

    }

    @CommandMethod("clan join <name>")
    fun onClanJoin(player: Player, @Argument(value = "name", suggestions = "clans") clan: String) {

    }

    @CommandMethod("clan leave")
    fun onClanLeave(player: Player, @Argument(value = "name") clan: String) {

    }

    @CommandMethod("clan members")
    fun onClanMembers(player: Player) {

    }

    @CommandMethod("clan raid")
    fun onClanRaid(player: Player) {

    }

    @CommandMethod("clan top")
    fun onClanTop(commandSender: CommandSender) {

    }
}