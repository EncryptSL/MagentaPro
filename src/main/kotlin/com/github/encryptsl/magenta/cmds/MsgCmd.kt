package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotation.specifier.Greedy
import cloud.commandframework.annotations.*
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.pm.PrivateMessageEvent
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class MsgCmd(private val magenta: Magenta) {

    @ProxiedBy("whisper")
    @Command("pm|tell <player> <message>")
    @Permission("magenta.msg")
    fun onMsgProxy(commandSender: CommandSender,
                   @Argument(value = "player", suggestions = "players") target: Player,
                   @Argument(value = "message") @Greedy message: String) {
        onMsg(commandSender, target, message)
    }

    @Command("msg|w <player> <message>")
    @Permission("magenta.msg")
    fun onMsg(commandSender: CommandSender,
        @Argument(value = "player", suggestions = "players") target: Player,
        @Argument(value = "message") @Greedy message: String
    ) {
        magenta.schedulerMagenta.doSync(magenta) {
            PrivateMessageEvent(commandSender, target, message).callEvent()
        }
    }
}