package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotation.specifier.Greedy
import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.Command
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.Permission
import club.minnced.discord.webhook.send.WebhookEmbed
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.extensions.avatar
import com.github.encryptsl.magenta.common.extensions.now
import com.github.encryptsl.magenta.common.extensions.trimUUID
import com.github.encryptsl.magenta.common.utils.ModernText
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class FeedbackCmd(private val magenta: Magenta) {

    @Command("feedback <message>")
    @Permission("magenta.feedback")
    fun onReport(
        player: Player,
        @Argument(value = "message") @Greedy message: String
    ) {
        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.feedback.success")))

        magenta.serverFeedback.client.send(magenta.serverFeedback.addEmbed {
            setTitle(WebhookEmbed.EmbedTitle("Zpětná vazba (#${magenta.random})", null))
            setColor(0x437ade)
            setDescription("Zpětná vazba od hráče ${player.name}")
            setThumbnailUrl(avatar.format(trimUUID(player.uniqueId)))
            addField(WebhookEmbed.EmbedField(false, "Zpráva", message))
            setFooter(WebhookEmbed.EmbedFooter("Vytvořeno ${now()}", null))
        })
    }
}