package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.GameMode
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandDescription("Provided by plugin MagentaPro")
class GmCmd(private val magenta: Magenta) {

    @CommandMethod("gamemode|gm <mode>")
    @CommandPermission("magenta.gamemode")
    fun onGameModeSelf(player: Player, @Argument(value = "mode", suggestions = "modes") gameMode: GameMode) {
        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.gamemode"), TagResolver.resolver(Placeholder.parsed("mode", gameMode.name))))
        player.gameMode = gameMode
    }

    @CommandMethod("gamemode|gm <target> <mode>")
    @CommandPermission("magenta.gamemode.other")
    fun onGameModeTarget(commandSender: CommandSender, @Argument(value = "target", suggestions = "online") target: Player, @Argument(value = "mode", suggestions = "gamemodes") gameMode: GameMode) {

        target.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.gamemode"), TagResolver.resolver(Placeholder.parsed("mode", gameMode.name))))
        target.gameMode = gameMode
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.gamemode.to"), TagResolver.resolver(
            Placeholder.parsed("player", target.name),
            Placeholder.parsed("mode", gameMode.name)
        )))

    }
}