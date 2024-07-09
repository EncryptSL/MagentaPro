package com.github.encryptsl.magenta.cmds

import club.minnced.discord.webhook.send.WebhookEmbed
import com.github.encryptsl.kmono.lib.api.commands.AnnotationFeatures
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.annotation.specifier.Greedy
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.annotations.*
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.paper.LegacyPaperCommandManager
import com.github.encryptsl.kmono.lib.extensions.now
import com.github.encryptsl.kmono.lib.extensions.toMinecraftAvatar
import com.github.encryptsl.magenta.Magenta
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("UNUSED")
class FeedbackCmd(private val magenta: Magenta) : AnnotationFeatures {

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: LegacyPaperCommandManager<CommandSender>
    ) {
        annotationParser.parse(this)
    }

    @Command("feedback <message>")
    @Permission("magenta.feedback")
    @CommandDescription("This command send feedback message to admins")
    fun onFeedback(
        player: Player,
        @Argument(value = "message") @Greedy message: String
    ) {
        player.sendMessage(magenta.locale.translation("magenta.command.feedback.success"))

        magenta.serverFeedback.addEmbed {
            setTitle(WebhookEmbed.EmbedTitle("Zpětná vazba (#${magenta.random})", null))
            setColor(0x437ade)
            setDescription("Zpětná vazba od hráče ${player.name}")
            setThumbnailUrl(player.uniqueId.toMinecraftAvatar())
            addField(WebhookEmbed.EmbedField(false, "Zpráva", message))
            setFooter(WebhookEmbed.EmbedFooter("Vytvořeno ${now()}", null))
        }?.let { magenta.serverFeedback.client.send(it) }
    }
}