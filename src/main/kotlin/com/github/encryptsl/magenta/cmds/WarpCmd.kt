package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.InfoType
import com.github.encryptsl.magenta.api.events.warp.*
import com.github.encryptsl.magenta.api.menu.warp.WarpGUI
import com.github.encryptsl.magenta.api.scheduler.SchedulerMagenta
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

@Suppress("UNUSED")
class WarpCmd(private val magenta: Magenta) {

    private val warpGUI: WarpGUI by lazy { WarpGUI(magenta) }

    @Command("setwarp <warp>")
    @CommandDescription("This command create your warp on location")
    @Permission("magenta.setwarp")
    fun onWarpCreate(player: Player, @Argument(value = "warp") warpName: String) {
        SchedulerMagenta.doSync(magenta) {
            magenta.server.pluginManager.callEvent(WarpCreateEvent(player, player.location, warpName))
        }
    }

    @Command("delwarp|dwarp <warp>")
    @Permission("magenta.delwarp")
    @CommandDescription("This command delete your warp")
    fun onWarpDelete(player: Player, @Argument("warp", suggestions = "warps") warpName: String) {
        SchedulerMagenta.doSync(magenta) {
            magenta.server.pluginManager.callEvent(WarpDeleteEvent(player, warpName))
        }
    }

    @Command("movewarp|mwarp <warp>")
    @Permission("magenta.move.warp")
    @CommandDescription("This command move warp to your current location")
    fun onWarpMoveLocation(player: Player, @Argument("warp", suggestions = "warps") warpName: String) {
        SchedulerMagenta.doSync(magenta) {
            magenta.server.pluginManager.callEvent(WarpMoveLocationEvent(player, player.location, warpName))
        }
    }

    @Command("renamewarp|rwarp <oldWarp> <newName>")
    @Permission("magenta.rename.warp")
    @CommandDescription("This command rename your warp")
    fun onWarpRename(player: Player, @Argument("oldWarp", suggestions = "warps") fromWarp: String, @Argument("newName") toWarpName: String) {
        SchedulerMagenta.doSync(magenta) {
            magenta.server.pluginManager.callEvent(WarpRenameEvent(player, fromWarp, toWarpName))
        }
    }
    @Command("warp <warp> [target]")
    @Permission("magenta.warp")
    @CommandDescription("This command teleport player to warp")
    fun onWarpTeleport(commandSender: CommandSender, @Argument("warp", suggestions = "warps") warpName: String, @Argument(value = "target", suggestions = "players") target: Player?) {
        SchedulerMagenta.doSync(magenta) {
            magenta.server.pluginManager.callEvent(WarpTeleportEvent(commandSender, target, warpName))
        }
    }

    @Command("warps|warplist")
    @Permission("magenta.warp.list")
    @CommandDescription("This command open warps gui or chat list")
    fun onWarps(commandSender: CommandSender) {
        if (commandSender is Player) {
            warpGUI.openMenu(commandSender)
        } else {
            magenta.server.pluginManager.callEvent(WarpInfoEvent(commandSender, null, InfoType.LIST))
        }
    }
}