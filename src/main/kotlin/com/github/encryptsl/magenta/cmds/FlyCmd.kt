package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.github.encryptsl.magenta.Magenta
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandDescription("Provided by plugin MagentaPro")
class FlyCmd(private val magenta: Magenta) {

    @CommandMethod("fly")
    @CommandPermission("magenta.fly")
    fun onFlySelf(player: Player) {
        if (player.isFlying) {
            player.isFlying = false
        } else {
            player.isFlying = true
        }
    }

    @CommandMethod("fly <target>")
    @CommandPermission("magenta.fly.other")
    fun onFlyTarget(commandSender: CommandSender, @Argument(value = "target", suggestions = "online") target: Player) {
        if (target.isFlying) {
            target.isFlying = false
        } else {
            target.isFlying = true
        }
    }

}