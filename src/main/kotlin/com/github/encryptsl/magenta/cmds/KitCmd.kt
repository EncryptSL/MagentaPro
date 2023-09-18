package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandDescription("Provided by plugin MagentaPro")
class KitCmd {

    @CommandMethod("kit <kit>")
    @CommandPermission("magenta.kit")
    fun onKit(player: Player, @Argument(value = "kit", suggestions = "kits") kit: String) {

    }

    @CommandMethod("kit <player> <kit>")
    @CommandPermission("magenta.kit.other")
    fun onKitOther(commandSender: CommandSender, player: Player, @Argument(value = "kit", suggestions = "kits") kit: String) {

    }

    @CommandMethod("createkit <kit>")
    @CommandPermission("magenta.createkit")
    fun onKitCreate(commandSender: CommandSender, player: Player, @Argument(value = "kit", suggestions = "kits") kit: String) {

    }

    @CommandMethod("deletekit <kit>")
    @CommandPermission("magenta.deletekit")
    fun onKitDelete(commandSender: CommandSender, player: Player, @Argument(value = "kit", suggestions = "kits") kit: String) {

    }



}