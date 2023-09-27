package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import cloud.commandframework.annotations.ProxiedBy
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.jail.JailCreateEvent
import com.github.encryptsl.magenta.api.events.jail.JailDeleteEvent
import com.github.encryptsl.magenta.api.events.jail.JailEvent
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandDescription("Provived by plugin MagentaPro")
class JailCmd(private val magenta: Magenta) {

    @CommandMethod("jail help")
    @CommandPermission("magenta.jail.help")
    fun onHelp(commandSender: CommandSender) {

    }

    @CommandMethod("jail player <jailName> <player> [time]")
    @CommandPermission("magenta.jail")
    fun onJailPlayer(commandSender: CommandSender, @Argument(value = "jailName", suggestions = "jails") jailName: String, @Argument(value = "player", suggestions = "offlinePlayers") offlinePlayer: OfflinePlayer, @Argument(value = "time", defaultValue = "120") time: Long) {
        magenta.schedulerMagenta.runTask(magenta) {
            magenta.pluginManager.callEvent(JailEvent(commandSender, jailName, offlinePlayer, time))
        }
    }

    @CommandMethod("jail create <name>")
    @CommandPermission("magenta.jail.create")
    fun onJailCreate(player: Player, @Argument(value = "name") jailName: String) {
        magenta.schedulerMagenta.runTask(magenta) {
            magenta.pluginManager.callEvent(JailCreateEvent(player, jailName, player.location))
        }
    }

    @CommandMethod("jail delete <name>")
    @CommandPermission("magenta.jail.delete")
    fun onJailDelete(commandSender: CommandSender, @Argument(value = "name") jailName: String) {
        magenta.schedulerMagenta.runTask(magenta) {
            magenta.pluginManager.callEvent(JailDeleteEvent(commandSender, jailName))
        }
    }

    @ProxiedBy("jails")
    @CommandMethod("jail list")
    @CommandPermission("magenta.jail.list")
    fun onJails(commandSender: CommandSender) {

    }

}