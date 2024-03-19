package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.hook.luckperms.LuckPermsAPI
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

@Suppress("UNUSED")
class FlyCmd(private val magenta: Magenta) {

    private val luckPermsAPI: LuckPermsAPI by lazy { LuckPermsAPI() }

    @Command("fly")
    @Permission("magenta.fly")
    @CommandDescription("This command enable or disable your flying")
    fun onFlySelf(player: Player) {
        magenta.commandHelper.allowFly(null, player)
    }

    @Command("fly <target>")
    @Permission("magenta.fly.other")
    @CommandDescription("This command enable or disable other player flying")
    fun onFlyTarget(commandSender: CommandSender, @Argument(value = "target", suggestions = "players") target: Player) {

        if (target.hasPermission("magenta.fly.modify.exempt")) return

        magenta.commandHelper.allowFly(commandSender, target)
    }

}