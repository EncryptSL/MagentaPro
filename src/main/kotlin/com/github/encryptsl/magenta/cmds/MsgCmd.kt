package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.kmono.lib.api.commands.AnnotationFeatures
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.annotation.specifier.Greedy
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.annotations.*
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.paper.LegacyPaperCommandManager
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.pm.FastReplyMessageEvent
import com.github.encryptsl.magenta.api.events.pm.PrivateMessageEvent
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

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
    fun onMsgProxy(
        player: Player,
        @Argument(value = "player", suggestions = "players") target: Player,
        @Argument(value = "message") @Greedy message: String
    ) {
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

    @Command("msgtoggle")
    @Permission("magenta.msg.toggle")
    @CommandDescription("This command enable or disable private messages")
    fun onMsgToggle(
        player: Player
    ) {
        val user = magenta.user.getUser(player.uniqueId)

        val toggled = magenta.commandHelper.isMsgToggled(user)

        player.sendMessage(magenta.locale.translation("magenta.command.msg.success.toggled", Placeholder.parsed("toggled", toggled.toString())))
    }

    @Command("msgtoggle <target> [toggle]")
    @Permission("magenta.msg.toggle.other")
    @CommandDescription("This command enable or disable private messages")
    fun onMsgToggleOther(
        commandSender: CommandSender,
        @Argument(value = "target", suggestions = "offlinePlayers") offlinePlayer: OfflinePlayer,
        @Argument(value = "toggle") @Default("true") toggle: Boolean
    ) {
        val user = magenta.user.getUser(offlinePlayer.uniqueId)

        if (user.getAccount().getBoolean("commands.toggle.msg"))
            return commandSender.sendMessage(magenta.locale.translation("magenta.command.msg.error.toggled.other", TagResolver.resolver(
                Placeholder.parsed("target", offlinePlayer.name.toString())
            )))

        user.set("commands.toggle.msg", toggle)

        commandSender.sendMessage(magenta.locale.translation("magenta.command.msg.success.toggled.other", TagResolver.resolver(
            Placeholder.parsed("target", offlinePlayer.name.toString()),
            Placeholder.parsed("toggled", toggle.toString())
        )))
    }
}