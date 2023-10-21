package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.CommandHelper
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class VanishCmd(private val magenta: Magenta) {

    private val commandHelper by lazy { CommandHelper(magenta) }

    @CommandMethod("vanish")
    @CommandPermission("magenta.vanish")
    fun onVanish(player: Player) {
        val user = magenta.user.getUser(player.uniqueId)
        val isVanished = user.getAccount().getBoolean("vanished")

        magenta.server.onlinePlayers.filter { p -> !p.hasPermission("magenta.vanish.exempt") }.forEach { players ->
            if (isVanished)
                players.showPlayer(magenta, player)
            else
                players.hidePlayer(magenta, player)
        }

        val mode = commandHelper.isVanished(isVanished)

        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.vanish.success.vanish"), Placeholder.parsed("mode", mode)))

        if (isVanished) {
            user.set("vanished", false)
        } else {
            user.set("vanished", true)
        }
    }

    @CommandMethod("vanish <player>")
    @CommandPermission("magenta.vanish.other")
    fun onVanishOther(commandSender: CommandSender, @Argument(value = "player", suggestions = "players") target: Player) {
        val user = magenta.user.getUser(target.uniqueId)
        val isVanished = user.getAccount().getBoolean("vanished")

        magenta.server.onlinePlayers.filter { p -> !p.hasPermission("magenta.vanish.exempt") }.forEach { players ->
            if (isVanished)
                players.showPlayer(magenta, target)
            else
                players.hidePlayer(magenta, target)
        }

        val mode = commandHelper.isVanished(isVanished)

        target.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.vanish.success.vanish"), Placeholder.parsed("mode", mode)))

        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.vanish.success.vanish.to"), TagResolver.resolver(
            Placeholder.parsed("player", target.name),
            Placeholder.parsed("mode", mode)
        )))

        if (isVanished) {
            user.set("vanished", false)
        } else {
            user.set("vanished", true)
        }
    }

}