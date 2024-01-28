package com.github.encryptsl.magenta.cmds

import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.InfoType
import com.github.encryptsl.magenta.api.events.warp.*
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("UNUSED")
class WarpCmd(private val magenta: Magenta) {

    @Command("setwarp <warp>")
    @Permission("magenta.setwarp")
    fun onWarpCreate(player: Player, @Argument(value = "warp") warpName: String) {
        magenta.schedulerMagenta.doSync(magenta) {
            magenta.server.pluginManager.callEvent(WarpCreateEvent(player, player.location, warpName))
        }
    }

    @Command("delwarp <warp>")
    @Permission("magenta.delwarp")
    fun onWarpDelete(player: Player, @Argument("warp", suggestions = "warps") warpName: String) {
        magenta.schedulerMagenta.doSync(magenta) {
            magenta.server.pluginManager.callEvent(WarpDeleteEvent(player, warpName))
        }
    }

    @Command("movewarp <warp>")
    @Permission("magenta.move.warp")
    fun onWarpMoveLocation(player: Player, @Argument("warp", suggestions = "warps") warpName: String) {
        magenta.schedulerMagenta.doSync(magenta) {
            magenta.server.pluginManager.callEvent(WarpMoveLocationEvent(player, player.location, warpName))
        }
    }

    @Command("renamewarp <oldWarp> <newName>")
    @Permission("magenta.rename.warp")
    fun onWarpRename(player: Player, @Argument("oldWarp", suggestions = "warps") fromWarp: String, @Argument("newName") toWarpName: String) {
        magenta.schedulerMagenta.doSync(magenta) {
            magenta.server.pluginManager.callEvent(WarpRenameEvent(player, fromWarp, toWarpName))
        }
    }
    @Command("warp <warp> [target]")
    @Permission("magenta.warp")
    fun onWarpTeleport(commandSender: CommandSender, @Argument("warp", suggestions = "warps") warpName: String, @Argument(value = "target", suggestions = "players") target: Player?) {
        magenta.schedulerMagenta.doSync(magenta) {
            magenta.server.pluginManager.callEvent(WarpTeleportEvent(commandSender, target, warpName))
        }
    }

    @Command("warps")
    @Permission("magenta.warp.list")
    fun onWarps(commandSender: CommandSender) {
        magenta.schedulerMagenta.doSync(magenta) {
            magenta.server.pluginManager.callEvent(WarpInfoEvent(commandSender, null, InfoType.LIST))
        }
    }
}