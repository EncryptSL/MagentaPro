package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import cloud.commandframework.annotations.ProxiedBy
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandDescription("Provided by plugin MagentaPro")
@CommandMethod("home")
class HomeCmd {

    @CommandMethod("tp <home>")
    @CommandPermission("magenta.home.tp")
    fun onHome(player: Player, @Argument(value = "home", suggestions = "homes") home: String) {

    }

    @CommandMethod("create <home>")
    @CommandPermission("magenta.home.create")
    fun onSetHome(player: Player, @Argument(value = "home") home: String) {

    }

    @CommandMethod("delete <home>")
    @CommandPermission("magenta.delhome")
    fun onDeleteHome(player: Player, @Argument(value = "home") home: String) {

    }

    @CommandMethod("rename <oldName> <newName>")
    @CommandPermission("magenta.home.rename")
    fun onRenameHome(player: Player, @Argument(value = "oldName") oldName: String, @Argument(value = "newName") newName: String) {

    }


    @ProxiedBy("homes")
    @CommandMethod("homes")
    @CommandPermission("magenta.home.list")
    fun onHomeList(commandSender: CommandSender) {

    }

}