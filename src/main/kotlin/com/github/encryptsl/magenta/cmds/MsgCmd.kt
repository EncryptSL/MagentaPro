package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.kmono.lib.api.commands.AnnotationFeatures
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.pm.FastReplyMessageEvent
import com.github.encryptsl.magenta.api.events.pm.PrivateMessageEvent
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotation.specifier.Greedy
import org.incendo.cloud.annotations.*
import org.incendo.cloud.paper.LegacyPaperCommandManager

@Suppress("UNUSED")
class MsgCmd(private val magenta: Magenta) : AnnotationFeatures {

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: LegacyPaperCommandManager<CommandSender>
    ) {
        annotationParser.parse(this)
    }

    @ProxiedBy("whisper")
    @Command("pm|tell <player> <message>")
    @Permission("magenta.msg")
    @CommandDescription("This command send to other player private message")
    fun onMsgProxy(player: Player,
                   @Argument(value = "player", suggestions = "players") target: Player,
                   @Argument(value = "message") @Greedy message: String) {
        onMsg(player, target, message)
    }

    @Command("msg|w <player> <message>")
    @Permission("magenta.msg")
    @CommandDescription("This command send to other player private message")
    fun onMsg(commandSender: CommandSender,
        @Argument(value = "player", suggestions = "players") target: Player,
        @Argument(value = "message") @Greedy message: String
    ) {
        PrivateMessageEvent(commandSender, target, message, magenta.playerCacheManager).callEvent()
    }

    @Command("reply|r <message>")
    @Permission("magenta.msg")
    @CommandDescription("This command send fast reply to current conversation")
    fun onFastReplyMsg(player: Player, @Argument("message") @Greedy message: String) {
        val receiverUUID = magenta.playerCacheManager.reply.getIfPresent(player)

        FastReplyMessageEvent(player, receiverUUID, message).callEvent()
    }
}