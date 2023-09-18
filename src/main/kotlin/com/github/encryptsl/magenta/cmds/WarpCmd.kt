package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import com.github.encryptsl.magenta.common.utils.ModernText
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandMethod("warp")
class WarpCmd {
    @CommandMethod("help|h")
    fun onHelp(commandSender: CommandSender) {

    }

    @CommandMethod("info|i <warp>")
    fun onInfo(commandSender: CommandSender, @Argument(value = "warp", suggestions = "warps") lynxName: String) {
    }

    @CommandMethod("create|c <warp>")
    fun onWarpCreate(player: Player, @Argument(value = "warp") lynxName: String) {

    }

    @CommandMethod("delete|d <warp>")
    fun onWarpDelete(player: Player, @Argument("warp", suggestions = "warps") lynxName: String) {

    }

    @CommandMethod("movehere|mh <warp>")
    fun onWarpMoveLocation(player: Player, @Argument("warp", suggestions = "warps") warp: String) {

    }

    @CommandMethod("rename|rn <oldName> <newName>")
    fun onWarpRename(player: Player, @Argument("oldWarp", suggestions = "warps") fromLynx: String, @Argument("newName") toLynxName: String) {

    }
    @CommandMethod("tp|t <warp>")
    fun onWarpTeleport(player: Player, @Argument("warp", suggestions = "warps") lynxName: String) {

    }
}