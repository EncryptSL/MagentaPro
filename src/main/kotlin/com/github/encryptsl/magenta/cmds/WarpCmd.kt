package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.InfoType
import com.github.encryptsl.magenta.api.events.warp.*
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("UNUSED")
class WarpCmd(private val magenta: Magenta) {

    @CommandMethod("setwarp <warp>")
    @CommandPermission("magenta.setwarp")
    fun onWarpCreate(player: Player, @Argument(value = "warp") warpName: String) {
        magenta.schedulerMagenta.doSync(magenta) {
            magenta.server.pluginManager.callEvent(WarpCreateEvent(player, player.location, warpName))
        }
    }

    @CommandMethod("delwarp <warp>")
    @CommandPermission("magenta.delwarp")
    fun onWarpDelete(player: Player, @Argument("warp", suggestions = "warps") warpName: String) {
        magenta.schedulerMagenta.doSync(magenta) {
            magenta.server.pluginManager.callEvent(WarpDeleteEvent(player, warpName))
        }
    }

    @CommandMethod("movewarp <warp>")
    @CommandPermission("magenta.move.warp")
    fun onWarpMoveLocation(player: Player, @Argument("warp", suggestions = "warps") warpName: String) {
        magenta.schedulerMagenta.doSync(magenta) {
            magenta.server.pluginManager.callEvent(WarpMoveLocationEvent(player, player.location, warpName))
        }
    }

    @CommandMethod("renamewarp <oldWarp> <newName>")
    @CommandPermission("magenta.rename.warp")
    fun onWarpRename(player: Player, @Argument("oldWarp", suggestions = "warps") fromWarp: String, @Argument("newName") toWarpName: String) {
        magenta.schedulerMagenta.doSync(magenta) {
            magenta.server.pluginManager.callEvent(WarpRenameEvent(player, fromWarp, toWarpName))
        }
    }
    @CommandMethod("warp <warp> [target]")
    @CommandPermission("magenta.warp")
    fun onWarpTeleport(commandSender: CommandSender, @Argument("warp", suggestions = "warps") warpName: String, @Argument(value = "target", suggestions = "players") target: Player?) {
        magenta.schedulerMagenta.doSync(magenta) {
            magenta.server.pluginManager.callEvent(WarpTeleportEvent(commandSender, target, warpName))
        }
    }

    @CommandMethod("warps")
    @CommandPermission("magenta.warp.list")
    fun onWarps(commandSender: CommandSender) {
        magenta.schedulerMagenta.doSync(magenta) {
            magenta.server.pluginManager.callEvent(WarpInfoEvent(commandSender, null, InfoType.LIST))
        }
    }
}