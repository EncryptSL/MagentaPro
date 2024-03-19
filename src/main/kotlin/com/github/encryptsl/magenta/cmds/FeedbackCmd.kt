package com.github.encryptsl.magenta.cmds

import club.minnced.discord.webhook.send.WebhookEmbed
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.extensions.now
import com.github.encryptsl.magenta.common.extensions.toMinotarAvatar
import com.github.encryptsl.magenta.common.utils.ModernText
import org.bukkit.entity.Player
import org.incendo.cloud.annotation.specifier.Greedy
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

@Suppress("UNUSED")
class FeedbackCmd(private val magenta: Magenta) {

    @Command("feedback <message>")
    @Permission("magenta.feedback")
    @CommandDescription("This command send feedback message to admins")
    fun onReport(
        player: Player,
        @Argument(value = "message") @Greedy message: String
    ) {
        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.feedback.success")))

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