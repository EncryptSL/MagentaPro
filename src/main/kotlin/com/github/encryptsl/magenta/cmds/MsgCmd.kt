package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.pm.FastReplyMessageEvent
import com.github.encryptsl.magenta.api.events.pm.PrivateMessageEvent
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotation.specifier.Greedy
import org.incendo.cloud.annotations.*

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class MsgCmd(private val magenta: Magenta) {

    @ProxiedBy("whisper")
    @Command("pm|tell <player> <message>")
    @Permission("magenta.msg")
    fun onMsgProxy(player: Player,
                   @Argument(value = "player", suggestions = "players") target: Player,
                   @Argument(value = "message") @Greedy message: String) {
        onMsg(player, target, message)
    }

    @Command("msg|w <player> <message>")
    @Permission("magenta.msg")
    fun onMsg(commandSender: CommandSender,
        @Argument(value = "player", suggestions = "players") target: Player,
        @Argument(value = "message") @Greedy message: String
    ) {
        PrivateMessageEvent(commandSender, target, message, magenta.playerCacheManager).callEvent()
    }

    @Command("reply|r <message>")
    @Permission("magenta.msg")
    fun onFastReplyMsg(commandSender: CommandSender, @Argument("message") @Greedy message: String) {
        val receiverUUID = magenta.playerCacheManager.reply.getIfPresent(commandSender)

        FastReplyMessageEvent(commandSender, receiverUUID, message).callEvent()
    }
}