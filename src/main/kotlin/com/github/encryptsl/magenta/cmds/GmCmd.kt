package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.kmono.lib.api.commands.AnnotationFeatures
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.annotations.*
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.paper.LegacyPaperCommandManager
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.suggestion.Suggestion
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.Permissions
import com.github.encryptsl.magenta.common.hook.luckperms.LuckPermsAPI
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.GameMode
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

@Suppress("UNUSED")
class GmCmd(private val magenta: Magenta) : AnnotationFeatures {

    private val luckPermsAPI: LuckPermsAPI by lazy { LuckPermsAPI() }

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: LegacyPaperCommandManager<CommandSender>
    ) {
        commandManager.parserRegistry().registerSuggestionProvider("gamemodes") { sender, _ ->
            return@registerSuggestionProvider CompletableFuture.completedFuture(
                GameMode.entries.filter { sender.hasPermission(Permissions.GAME_MODES.format(it.name.lowercase())) }.map { Suggestion.suggestion(it.name) }
            )
        }
        annotationParser.parse(this)
    }

    @Command("gamemode|gm <mode>")
    @Permission("magenta.gamemode")
    @CommandDescription("This command switch your gamemode")
    fun onGameModeSelf(player: Player, @Argument(value = "mode", suggestions = "gamemodes") gameMode: GameMode) {
        if (!player.hasPermission(Permissions.GAME_MODES.format(gameMode.name.lowercase())))
            return player.sendMessage(magenta.locale.translation("magenta.command.gamemode.error.not.permission",
                Placeholder.parsed("gamemode", gameMode.name)
            ))

        player.sendMessage(magenta.locale.translation("magenta.command.gamemode", Placeholder.parsed("gamemode", gameMode.name)))
        setGameModeToTarget(player, gameMode)
    }

    @Command("gamemode|gm <mode> <target>")
    @Permission("magenta.gamemode.other")
    @CommandDescription("This command switch gamemode to other player")
    fun onGameModeTarget(commandSender: CommandSender, @Argument(value = "target", suggestions = "players") target: Player, @Argument(value = "mode", suggestions = "gamemodes") gameMode: GameMode) {
        setGameModeProxy(commandSender, target, gameMode)
    }

    @Command("gma")
    @Permission("magenta.gamemode")
    @CommandDescription("This command switch your gamemode on adventure")
    fun onGameModeSelfAdventure(player: Player) {
        val gamemode = GameMode.ADVENTURE

        if (!player.hasPermission(Permissions.GAME_MODES.format(gamemode.name.lowercase())))
            return player.sendMessage(magenta.locale.translation("magenta.command.gamemode.error.not.permission",
                Placeholder.parsed("gamemode", gamemode.name)
            ))
        player.sendMessage(magenta.locale.translation("magenta.command.gamemode", Placeholder.parsed("gamemode", gamemode.name)))

        setGameModeToTarget(player, gamemode)
    }

    @Command("gmc")
    @Permission("magenta.gamemode")
    @CommandDescription("This command switch your gamemode on creative")
    fun onGameModeSelfCreative(player: Player) {
        val gamemode = GameMode.CREATIVE

        if (!player.hasPermission(Permissions.GAME_MODES.format(gamemode.name.lowercase())))
            return player.sendMessage(magenta.locale.translation("magenta.command.gamemode.error.not.permission",
                Placeholder.parsed("gamemode", gamemode.name)
            ))
        player.sendMessage(magenta.locale.translation("magenta.command.gamemode", Placeholder.parsed("gamemode", gamemode.name)))

        setGameModeToTarget(player, gamemode)
    }

    @Command("gmsp")
    @Permission("magenta.gamemode")
    @CommandDescription("This command switch your gamemode on spectator")
    fun onGameModeSelfSpectator(player: Player) {
        val gamemode = GameMode.SPECTATOR

        if (!player.hasPermission(Permissions.GAME_MODES.format(gamemode.name.lowercase())))
            return player.sendMessage(magenta.locale.translation("magenta.command.gamemode.error.not.permission",
                Placeholder.parsed("gamemode", gamemode.name)
            ))
        player.sendMessage(magenta.locale.translation("magenta.command.gamemode", Placeholder.parsed("gamemode", gamemode.name)))

        setGameModeToTarget(player, gamemode)
    }

    @Command("gms")
    @Permission("magenta.gamemode")
    @CommandDescription("This command switch your gamemode on survival")
    fun onGameModeSelfSurvival(player: Player) {
        val gamemode = GameMode.SURVIVAL

        if (!player.hasPermission(Permissions.GAME_MODES.format(gamemode.name.lowercase())))
            return player.sendMessage(magenta.locale.translation("magenta.command.gamemode.error.not.permission",
                Placeholder.parsed("gamemode", gamemode.name)
            ))
        player.sendMessage(magenta.locale.translation("magenta.command.gamemode", Placeholder.parsed("gamemode", gamemode.name)))

        setGameModeToTarget(player, gamemode)
    }

    private fun setGameModeProxy(commandSender: CommandSender, target: Player, gameMode: GameMode) {
        val isSenderConsole = commandSender is ConsoleCommandSender

        if (isSenderConsole || commandSender.isOp) {
            setGameModeByConsoleOrOperator(commandSender, target, gameMode)
        } else {
            if (target.hasPermission("magenta.gamemode.modify.exempt")) return

            setGameModeToTarget(target, gameMode)
            sendMessages(commandSender, target, gameMode)
        }
    }

    private fun setGameModeByConsoleOrOperator(commandSender: CommandSender, target: Player, gameMode: GameMode) {
        setGameModeToTarget(target, gameMode)
        sendMessages(commandSender, target, gameMode)
    }

    private fun sendMessages(commandSender: CommandSender, player: Player, gameMode: GameMode) {
        player.sendMessage(magenta.locale.translation("magenta.command.gamemode", Placeholder.parsed("gamemode", gameMode.name)))
        commandSender.sendMessage(magenta.locale.translation("magenta.command.gamemode.to", TagResolver.resolver(
            Placeholder.parsed("player", player.name),
            Placeholder.parsed("gamemode", gameMode.name)
        )))
    }

    private fun setGameModeToTarget(player: Player, gameMode: GameMode) {
        player.gameMode = gameMode
        magenta.user.getUser(player.uniqueId).set("gamemode", gameMode.name)
    }
}