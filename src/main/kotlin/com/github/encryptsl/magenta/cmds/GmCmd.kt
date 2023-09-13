package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ModernText
import org.bukkit.GameMode
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandDescription("Provided by plugin MagentaPro")
class GmCmd(private val magenta: Magenta) {

    @CommandMethod("gamemode|gm <mode>")
    @CommandPermission("magenta.gamemode")
    fun onGamemodeSelf(player: Player, @Argument(value = "mode", suggestions = "modes") gameMode: GameMode) {
        when(gameMode) {
            GameMode.CREATIVE -> {
                player.gameMode = gameMode
            }
            GameMode.SURVIVAL -> {
                player.gameMode = gameMode
            }
            GameMode.ADVENTURE -> {
                player.gameMode = gameMode
            }
            GameMode.SPECTATOR -> {
                player.gameMode = gameMode
            }
        }
    }

    @CommandMethod("gamemode|gm <target> <mode>")
    @CommandPermission("magenta.gamemode.other")
    fun onGamemodeTarget(commandSender: CommandSender, @Argument(value = "target", suggestions = "online") target: Player, @Argument(value = "mode", suggestions = "gamemodes") gameMode: GameMode) {
        when(gameMode) {
            GameMode.CREATIVE -> {
                commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("")))
                target.gameMode = gameMode
            }
            GameMode.SURVIVAL -> {
                target.gameMode = gameMode
            }
            GameMode.ADVENTURE -> {
                target.gameMode = gameMode
            }
            GameMode.SPECTATOR -> {
                target.gameMode = gameMode
            }
        }

    }
}