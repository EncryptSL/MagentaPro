package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.Command
import cloud.commandframework.annotations.Permission
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.CommandHelper
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class FlyCmd(magenta: Magenta) {

    private val commandHelper: CommandHelper by lazy { CommandHelper(magenta) }

    @Command("fly")
    @Permission("magenta.fly")
    fun onFlySelf(player: Player) {
        commandHelper.allowFly(null, player)
    }

    @Command("fly <target>")
    @Permission("magenta.fly.other")
    fun onFlyTarget(commandSender: CommandSender, @Argument(value = "target", suggestions = "players") target: Player) {
        commandHelper.allowFly(commandSender, target)
    }

}