package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.CommandHelper
import com.github.encryptsl.magenta.common.hook.luckperms.LuckPermsAPI
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class FlyCmd(magenta: Magenta) {

    private val commandHelper: CommandHelper by lazy { CommandHelper(magenta) }
    private val luckPermsAPI: LuckPermsAPI by lazy { LuckPermsAPI() }

    @Command("fly")
    @Permission("magenta.fly")
    fun onFlySelf(player: Player) {
        commandHelper.allowFly(null, player)
    }

    @Command("fly <target>")
    @Permission("magenta.fly.other")
    fun onFlyTarget(commandSender: CommandSender, @Argument(value = "target", suggestions = "players") target: Player) {

        if (target.hasPermission("magenta.fly.modify.exempt")) return

        commandHelper.allowFly(commandSender, target)
    }

}