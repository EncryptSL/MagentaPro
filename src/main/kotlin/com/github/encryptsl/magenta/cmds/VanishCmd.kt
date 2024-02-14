package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.CommandHelper
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class VanishCmd(private val magenta: Magenta) {

    private val commandHelper by lazy { CommandHelper(magenta) }

    @Command("vanish")
    @Permission("magenta.vanish")
    fun onVanish(player: Player) {
        val user = magenta.user.getUser(player.uniqueId)

        magenta.server.onlinePlayers.filter { p -> !p.hasPermission("magenta.vanish.exempt") }.forEach { players ->
            commandHelper.doVanish(players, player, user.isVanished())
        }

        val mode = commandHelper.isVanished(user.isVanished())

        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.vanish.success.vanish"), Placeholder.parsed("mode", mode)))

        if (user.isVanished()) {
            user.set("vanished", false)
        } else {
            user.set("vanished", true)
        }
    }

    @Command("vanish <player>")
    @Permission("magenta.vanish.other")
    fun onVanishOther(commandSender: CommandSender, @Argument(value = "player", suggestions = "players") target: Player) {
        val user = magenta.user.getUser(target.uniqueId)

        magenta.server.onlinePlayers.filter { p -> !p.hasPermission("magenta.vanish.exempt") }.forEach { players ->
            commandHelper.doVanish(players, target, user.isVanished())
        }

        val mode = commandHelper.isVanished(user.isVanished())

        target.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.vanish.success.vanish"), Placeholder.parsed("mode", mode)))

        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.vanish.success.vanish.to"), TagResolver.resolver(
            Placeholder.parsed("player", target.name),
            Placeholder.parsed("mode", mode)
        )))

        if (user.isVanished()) {
            user.set("vanished", false)
        } else {
            user.set("vanished", true)
        }
    }

}