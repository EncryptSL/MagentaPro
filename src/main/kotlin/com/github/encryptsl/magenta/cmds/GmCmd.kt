package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.scheduler.SchedulerMagenta
import com.github.encryptsl.magenta.common.hook.luckperms.LuckPermsAPI
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.GameMode
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class GmCmd(private val magenta: Magenta) {

    private val luckPermsAPI: LuckPermsAPI by lazy { LuckPermsAPI() }

    @Command("gamemode|gm <mode>")
    @Permission("magenta.gamemode")
    fun onGameModeSelf(player: Player, @Argument(value = "mode", suggestions = "gamemodes") gameMode: GameMode) {
        if (!player.hasPermission("magenta.gamemodes.${gameMode.name.lowercase()}"))
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.gamemode.error.not.permission"), TagResolver.resolver(
                Placeholder.parsed("gamemode", gameMode.name)
            )))

        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.gamemode"), TagResolver.resolver(Placeholder.parsed("gamemode", gameMode.name))))
        setGameModeToTarget(player, gameMode)
    }

    @Command("gamemode|gm <mode> <target>")
    @Permission("magenta.gamemode.other")
    fun onGameModeTarget(commandSender: CommandSender, @Argument(value = "target", suggestions = "players") target: Player, @Argument(value = "mode", suggestions = "gamemodes") gameMode: GameMode) {
        setGameModeProxy(commandSender, target, gameMode)
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
        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.gamemode"), TagResolver.resolver(Placeholder.parsed("gamemode", gameMode.name))))
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.gamemode.to"), TagResolver.resolver(
            Placeholder.parsed("player", player.name),
            Placeholder.parsed("gamemode", gameMode.name)
        )))
    }

    private fun setGameModeToTarget(player: Player, gameMode: GameMode) {
        SchedulerMagenta.doSync(magenta) {
            player.gameMode = gameMode
        }
    }
}