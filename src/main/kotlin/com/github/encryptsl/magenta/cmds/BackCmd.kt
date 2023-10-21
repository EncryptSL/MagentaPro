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
import org.bukkit.event.player.PlayerTeleportEvent

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class BackCmd(private val magenta: Magenta) {

    @CommandMethod("back")
    @CommandPermission("magenta.back")
    fun onBack(player: Player) {
        val playerAccount = PlayerAccount(magenta, player.uniqueId)
        magenta.schedulerMagenta.doSync(magenta){
            player.teleport(playerAccount.getLastLocation(), PlayerTeleportEvent.TeleportCause.COMMAND)
        }
        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.back.success")))
    }

    @CommandMethod("back <player>")
    @CommandPermission("magenta.back.other")
    fun onBack(commandSender: CommandSender, @Argument(value = "player", suggestions = "players") target: Player) {
        val playerAccount = PlayerAccount(magenta, target.uniqueId)

        magenta.schedulerMagenta.doSync(magenta) {
            target.teleport(playerAccount.getLastLocation(), PlayerTeleportEvent.TeleportCause.COMMAND)
        }
        target.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.back.success")))
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.back.success.to"),
            Placeholder.parsed("player", target.name)
        ))
    }

}