package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import cloud.commandframework.annotations.specifier.Greedy
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.pm.PlayerPrivateMessageEvent
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandDescription("Provided by plugin MagentaPro")
class MsgCmd(private val magenta: Magenta) {

    @CommandMethod("pm|tell <player> <message>")
    @CommandPermission("magenta.msg")
    fun onMsgProxy(commandSender: CommandSender,
                   @Argument(value = "player", suggestions = "players") target: Player,
                   @Argument(value = "messsage")
                   @Greedy message: String) {
        onMsg(commandSender, target, message)
    }

    @CommandMethod("msg|w <player> <message>")
    @CommandPermission("magenta.msg")
    fun onMsg(commandSender: CommandSender,
        @Argument(value = "player", suggestions = "players") target: Player,
        @Argument(value = "messsage")
        @Greedy message: String
    ) {
        magenta.schedulerMagenta.runTask(magenta) {
            magenta.pluginManager.callEvent(PlayerPrivateMessageEvent(commandSender, target, message))
        }
    }
}