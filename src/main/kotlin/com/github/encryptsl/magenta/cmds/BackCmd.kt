package com.github.encryptsl.magenta.cmds

import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.account.UserAccount
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class BackCmd(private val magenta: Magenta) {

    @Command("back")
    @Permission("magenta.back")
    fun onBack(player: Player) {
        val userAccount = UserAccount(magenta, player.uniqueId)
        magenta.schedulerMagenta.doSync(magenta){
            player.teleport(userAccount.getLastLocation(), PlayerTeleportEvent.TeleportCause.COMMAND)
        }
        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.back.success")))
    }

    @Command("back <player>")
    @Permission("magenta.back.other")
    fun onBack(commandSender: CommandSender, @Argument(value = "player", suggestions = "players") target: Player) {
        val userAccount = UserAccount(magenta, target.uniqueId)

        magenta.schedulerMagenta.doSync(magenta) {
            target.teleport(userAccount.getLastLocation(), PlayerTeleportEvent.TeleportCause.COMMAND)
        }
        target.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.back.success")))
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.back.success.to"),
            Placeholder.parsed("player", target.name)
        ))
    }

}