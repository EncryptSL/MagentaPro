package com.github.encryptsl.magenta.cmds

import club.minnced.discord.webhook.send.WebhookEmbed
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.commands.AnnotationFeatures
import com.github.encryptsl.magenta.common.extensions.now
import com.github.encryptsl.magenta.common.extensions.toMinotarAvatar
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotation.specifier.Greedy
import org.incendo.cloud.annotations.*
import org.incendo.cloud.paper.PaperCommandManager

@Suppress("UNUSED")
class FeedbackCmd(private val magenta: Magenta) : AnnotationFeatures {

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: PaperCommandManager<CommandSender>
    ) {
        annotationParser.parse(this)
    }

    @Command("feedback <message>")
    @Permission("magenta.feedback")
    @CommandDescription("This command send feedback message to admins")
    fun onReport(
        player: Player,
        @Argument(value = "message") @Greedy message: String
    ) {
        player.sendMessage(magenta.localeConfig.translation("magenta.command.feedback.success"))

        magenta.serverFeedback.addEmbed {
            setTitle(WebhookEmbed.EmbedTitle("Zpětná vazba (#${magenta.random})", null))
            setColor(0x437ade)
            setDescription("Zpětná vazba od hráče ${player.name}")
            setThumbnailUrl(player.uniqueId.toMinotarAvatar())
            addField(WebhookEmbed.EmbedField(false, "Zpráva", message))
            setFooter(WebhookEmbed.EmbedFooter("Vytvořeno ${now()}", null))
        }?.let { magenta.serverFeedback.client.send(it) }
    }
}