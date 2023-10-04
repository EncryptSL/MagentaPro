package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.account.PlayerAccount
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class VanishCmd(private val magenta: Magenta) {

    @CommandMethod("vanish")
    @CommandPermission("magenta.vanish")
    fun onVanish(player: Player) {
        val account = PlayerAccount(magenta, player.uniqueId)
        if (account.getAccount().getBoolean("vanished")) {
            account.set("vanished", false)
            player.showPlayer(magenta, player)
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.vanish.success.unvanished")))
        } else {
            account.set("vanished", true)
            player.hidePlayer(magenta, player)
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.vanish.success.vanished")))
        }
    }

    @CommandMethod("vanish <player>")
    @CommandPermission("magenta.vanish.other")
    fun onVanishOther(commandSender: CommandSender, @Argument(value = "player", suggestions = "players") target: Player) {
        val account = PlayerAccount(magenta, target.uniqueId)
        if (account.getAccount().getBoolean("vanished")) {
            account.set("vanished", false)
            target.showPlayer(magenta, target)
            target.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.vanish.success.unvanished")))
            commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.vanish.success.unvanished.to"),
                Placeholder.parsed("player", target.name)
            ))
        } else {
            account.set("vanished", true)
            target.hidePlayer(magenta, target)
            target.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.vanish.success.vanished")))
            commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.vanish.success.vanished.to"),
                Placeholder.parsed("player", target.name)
            ))
        }
    }

}