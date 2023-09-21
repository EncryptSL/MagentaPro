package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.ProxiedBy
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ModernText
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandMethod("warp")
class WarpCmd(private val magenta: Magenta) {
    @CommandMethod("help|h")
    fun onHelp(commandSender: CommandSender) {

    }

    @CommandMethod("info|i <warp>")
    fun onInfo(commandSender: CommandSender, @Argument(value = "warp", suggestions = "warps") lynxName: String) {
    }

    @CommandMethod("create|c <warp>")
    fun onWarpCreate(player: Player, @Argument(value = "warp") lynxName: String) {
        //magenta.server.pluginManager.callEvent(WarpCreateEvent)
    }

    @CommandMethod("delete|d <warp>")
    fun onWarpDelete(player: Player, @Argument("warp", suggestions = "warps") lynxName: String) {
        //magenta.server.pluginManager.callEvent(WarpDeleteEvent)
    }

    @CommandMethod("movehere|mh <warp>")
    fun onWarpMoveLocation(player: Player, @Argument("warp", suggestions = "warps") warp: String) {
        //magenta.server.pluginManager.callEvent(WarpMoveLocationEvent)
    }

    @CommandMethod("rename|rn <oldName> <newName>")
    fun onWarpRename(player: Player, @Argument("oldWarp", suggestions = "warps") fromLynx: String, @Argument("newName") toLynxName: String) {
        //magenta.server.pluginManager.callEvent(WarpRenameEvent)
    }
    @CommandMethod("tp|t <warp>")
    fun onWarpTeleport(player: Player, @Argument("warp", suggestions = "warps") lynxName: String) {
        //magenta.server.pluginManager.callEvent(WarpTeleportEvent)
    }

    @ProxiedBy("warps")
    @CommandMethod("warps")
    fun onWarps(commandSender: CommandSender) {
        val list = magenta.warpModel.getWarps().joinToString { s -> "${s.warpName}," }
        commandSender.sendMessage(ModernText.miniModernText("<gray> $list"))
    }
}