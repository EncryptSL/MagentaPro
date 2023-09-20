package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.github.encryptsl.magenta.Magenta
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.time.Duration

@CommandDescription("Provived by plugin MagentaPro")
class JailCmd(private val magenta: Magenta) {

    @CommandMethod("jail help")
    @CommandPermission("magenta.jail.help")
    fun onHelp(commandSender: CommandSender) {

    }

    @CommandMethod("jail <player> [time]")
    @CommandPermission("magenta.jail")
    fun onJailPlayer(commandSender: CommandSender, @Argument(value = "player", suggestions = "offlinePlayers") offlinePlayer: OfflinePlayer, @Argument(value = "time", description = "", suggestions = "times") duration: Duration) {

    }

    @CommandMethod("jail create <name>")
    @CommandPermission("magenta.jail.create")
    fun onJailCreate(player: Player, @Argument(value = "name") name: String) {

    }

    @CommandMethod("jail delete <name>")
    @CommandPermission("magenta.jail.delete")
    fun onJailDelete(player: Player, @Argument(value = "name") name: String) {

    }

    @CommandMethod("jails")
    @CommandPermission("magenta.jails")
    fun onJails(commandSender: CommandSender) {

    }

}