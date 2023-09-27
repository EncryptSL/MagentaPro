package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import cloud.commandframework.annotations.ProxiedBy
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.InfoType
import com.github.encryptsl.magenta.api.events.warp.*
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandMethod("warp")
class WarpCmd(private val magenta: Magenta) {
    @CommandMethod("help|h")
    fun onHelp(commandSender: CommandSender) {

    }

    @CommandMethod("info|i <warp>")
    fun onInfo(commandSender: CommandSender, @Argument(value = "warp", suggestions = "warps") warpName: String) {
        magenta.schedulerMagenta.runTask(magenta) {
            magenta.server.pluginManager.callEvent(WarpInfoEvent(commandSender, warpName, InfoType.INFO))
        }
    }

    @CommandMethod("create|c <warp>")
    @CommandPermission("magenta.warp.create")
    fun onWarpCreate(player: Player, @Argument(value = "warp") warpName: String) {
        magenta.schedulerMagenta.runTask(magenta) {
            magenta.server.pluginManager.callEvent(WarpCreateEvent(player, player.location, warpName))
        }
    }

    @CommandMethod("delete|d <warp>")
    @CommandPermission("magenta.warp.delete")
    fun onWarpDelete(player: Player, @Argument("warp", suggestions = "warps") warpName: String) {
        magenta.schedulerMagenta.runTask(magenta) {
            magenta.server.pluginManager.callEvent(WarpDeleteEvent(player, warpName))
        }
    }

    @CommandMethod("movehere|mh <warp>")
    @CommandPermission("magenta.warp.move.here")
    fun onWarpMoveLocation(player: Player, @Argument("warp", suggestions = "warps") warpName: String) {
        magenta.schedulerMagenta.runTask(magenta) {
            magenta.server.pluginManager.callEvent(WarpMoveLocationEvent(player, player.location, warpName))
        }
    }

    @CommandMethod("rename|rn <oldWarp> <newName>")
    @CommandPermission("magenta.warp.rename")
    fun onWarpRename(player: Player, @Argument("oldWarp", suggestions = "warps") fromWarp: String, @Argument("newName") toWarpName: String) {
        magenta.schedulerMagenta.runTask(magenta) {
            magenta.server.pluginManager.callEvent(WarpRenameEvent(player, fromWarp, toWarpName))
        }
    }
    @CommandMethod("tp|t <warp> [target]")
    @CommandPermission("magenta.warp")
    fun onWarpTeleport(commandSender: CommandSender, @Argument("warp", suggestions = "warps") warpName: String, @Argument(value = "target", suggestions = "players") target: Player?) {
        magenta.schedulerMagenta.runTask(magenta) {
            magenta.server.pluginManager.callEvent(WarpTeleportEvent(commandSender, target, warpName))
        }
    }

    @ProxiedBy("warps")
    @CommandMethod("warps")
    @CommandPermission("magenta.warp.list")
    fun onWarps(commandSender: CommandSender) {
        magenta.schedulerMagenta.runTask(magenta) {
            magenta.server.pluginManager.callEvent(WarpInfoEvent(commandSender, null, InfoType.LIST))
        }
    }
}