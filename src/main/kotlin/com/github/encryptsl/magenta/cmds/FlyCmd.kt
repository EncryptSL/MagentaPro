package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class FlyCmd(private val magenta: Magenta) {

    @CommandMethod("fly")
    @CommandPermission("magenta.fly")
    fun onFlySelf(player: Player) {
        if (player.isFlying) {
            player.allowFlight = false
            player.isFlying = false
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.fly.success.deactivated")))
        } else {
            player.allowFlight = true
            player.isFlying = true
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.fly.success.activated")))
        }
    }

    @CommandMethod("fly <target>")
    @CommandPermission("magenta.fly.other")
    fun onFlyTarget(commandSender: CommandSender, @Argument(value = "target", suggestions = "players") target: Player) {
        if (target.isFlying) {
            target.isFlying = false
            target.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.fly.success.deactivated")))
            commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.fly.success.deactivated.to"), TagResolver.resolver(Placeholder.parsed("player", target.name))))
        } else {
            target.isFlying = true
            target.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.fly.deactivated")))
            commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.fly.success.activated.to"), TagResolver.resolver(Placeholder.parsed("player", target.name))))
        }
    }

}