package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.*
import cloud.commandframework.annotations.specifier.Greedy
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.pm.PlayerPrivateMessageEvent
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class MsgCmd(private val magenta: Magenta) {

    @ProxiedBy("whisper")
    @CommandMethod("pm|tell <player> <message>")
    @CommandPermission("magenta.msg")
    fun onMsgProxy(commandSender: CommandSender,
                   @Argument(value = "player", suggestions = "players") target: Player,
                   @Argument(value = "message") @Greedy message: String) {
        onMsg(commandSender, target, message)
    }

    @CommandMethod("msg|w <player> <message>")
    @CommandPermission("magenta.msg")
    fun onMsg(commandSender: CommandSender,
        @Argument(value = "player", suggestions = "players") target: Player,
        @Argument(value = "message") @Greedy message: String
    ) {
        magenta.schedulerMagenta.runTask(magenta) {
            val privateMessageEvent = PlayerPrivateMessageEvent(commandSender, target, message)
            privateMessageEvent.callEvent()
        }
    }
}