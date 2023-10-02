package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.CommandHelper
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class FlyCmd(magenta: Magenta) {

    private val commandHelper = CommandHelper(magenta)

    @CommandMethod("fly")
    @CommandPermission("magenta.fly")
    fun onFlySelf(player: Player) {
        commandHelper.allowFly(null, player)
    }

    @CommandMethod("fly <target>")
    @CommandPermission("magenta.fly.other")
    fun onFlyTarget(commandSender: CommandSender, @Argument(value = "target", suggestions = "players") target: Player) {
        commandHelper.allowFly(commandSender, target)
    }

}