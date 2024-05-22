package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.kmono.lib.api.commands.AnnotationFeatures
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.InfoType
import com.github.encryptsl.magenta.api.events.warp.*
import com.github.encryptsl.magenta.api.menu.modules.warp.WarpGUI
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.*
import org.incendo.cloud.paper.LegacyPaperCommandManager
import org.incendo.cloud.suggestion.Suggestion
import java.util.concurrent.CompletableFuture

@Suppress("UNUSED")
class WarpCmd(private val magenta: Magenta) : AnnotationFeatures {

    private val warpGUI: WarpGUI by lazy { WarpGUI(magenta) }

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: LegacyPaperCommandManager<CommandSender>
    ) {
        commandManager.parserRegistry().registerSuggestionProvider("warps") {_, _ ->
            return@registerSuggestionProvider CompletableFuture
                .completedFuture(magenta.warpModel.getWarps().join().map { s -> Suggestion.suggestion(s.warpName) })
        }
        annotationParser.parse(this)
    }

    @Command("setwarp <warp>")
    @CommandDescription("This command create your warp on location")
    @Permission("magenta.setwarp")
    fun onWarpCreate(player: Player, @Argument(value = "warp") warpName: String) {
        magenta.pluginManager.callEvent(WarpCreateEvent(player, player.location, warpName))
    }

    @Command("delwarp|dwarp <warp>")
    @Permission("magenta.delwarp")
    @CommandDescription("This command delete your warp")
    fun onWarpDelete(player: Player, @Argument("warp", suggestions = "warps") warpName: String) {
        magenta.pluginManager.callEvent(WarpDeleteEvent(player, warpName))
    }

    @Command("movewarp|mwarp <warp>")
    @Permission("magenta.move.warp")
    @CommandDescription("This command move warp to your current location")
    fun onWarpMoveLocation(player: Player, @Argument("warp", suggestions = "warps") warpName: String) {
        magenta.pluginManager.callEvent(WarpMoveLocationEvent(player, player.location, warpName))
    }

    @Command("renamewarp|rwarp <oldWarp> <newName>")
    @Permission("magenta.rename.warp")
    @CommandDescription("This command rename your warp")
    fun onWarpRename(player: Player, @Argument("oldWarp", suggestions = "warps") fromWarp: String, @Argument("newName") toWarpName: String) {
        magenta.pluginManager.callEvent(WarpRenameEvent(player, fromWarp, toWarpName))
    }
    @Command("warp <warp> [target]")
    @Permission("magenta.warp")
    @CommandDescription("This command teleport player to warp")
    fun onWarpTeleport(commandSender: CommandSender, @Argument("warp", suggestions = "warps") warpName: String, @Argument(value = "target", suggestions = "players") target: Player?) {
        magenta.pluginManager.callEvent(WarpTeleportEvent(commandSender, target, warpName))
    }

    @Command("warps|warplist")
    @Permission("magenta.warp.list")
    @CommandDescription("This command open warps gui or chat list")
    fun onWarps(commandSender: CommandSender) {
        if (commandSender is Player) {
            //warpGUI.openMenu(commandSender)
        } else {
            magenta.pluginManager.callEvent(WarpInfoEvent(commandSender, null, InfoType.LIST))
        }
    }
}