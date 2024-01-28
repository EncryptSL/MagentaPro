package com.github.encryptsl.magenta.cmds

import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.GameMode
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class GmCmd(private val magenta: Magenta) {

    @Command("gamemode|gm <mode>")
    @Permission("magenta.gamemode")
    fun onGameModeSelf(player: Player, @Argument(value = "mode", suggestions = "gamemodes") gameMode: GameMode) {
        if (!player.hasPermission("magenta.gamemodes.${gameMode.name.lowercase()}"))
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.gamemode.error.not.permission"), TagResolver.resolver(
                Placeholder.parsed("gamemode", gameMode.name)
            )))

        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.gamemode"), TagResolver.resolver(Placeholder.parsed("gamemode", gameMode.name))))
        magenta.schedulerMagenta.doSync(magenta) {
            player.gameMode = gameMode
        }
    }

    @Command("gamemode|gm <mode> <target>")
    @Permission("magenta.gamemode.other")
    fun onGameModeTarget(commandSender: CommandSender, @Argument(value = "target", suggestions = "players") target: Player, @Argument(value = "mode", suggestions = "gamemodes") gameMode: GameMode) {
        magenta.schedulerMagenta.doSync(magenta) {
            target.gameMode = gameMode
        }
        target.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.gamemode"), TagResolver.resolver(Placeholder.parsed("gamemode", gameMode.name))))
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.gamemode.to"), TagResolver.resolver(
            Placeholder.parsed("player", target.name),
            Placeholder.parsed("gamemode", gameMode.name)
        )))
    }
}