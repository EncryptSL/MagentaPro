package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import cloud.commandframework.annotations.specifier.Greedy
import club.minnced.discord.webhook.send.WebhookEmbed
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.report.ReportCategories
import com.github.encryptsl.magenta.common.extensions.avatar
import com.github.encryptsl.magenta.common.extensions.now
import com.github.encryptsl.magenta.common.extensions.trimUUID
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandDescription("Provided by plugin EncryptSL")
class ReportCmd(private val magenta: Magenta) {

    @CommandMethod("report <player> <category> [message]")
    @CommandPermission("magenta.report")
    fun onReport(
        player: Player,
        @Argument(value = "player", suggestions = "offlinePlayers") target: OfflinePlayer,
        @Argument(value = "category", suggestions = "reportCategories") category: ReportCategories,
        @Argument(value = "message", defaultValue = "Zpráva není specifikovaná.") @Greedy message: String
    ) {
        if (player.name == target.name)
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.report.error.yourself")))

        if (magenta.stringUtils.inInList("exempt-blacklist", target.name.toString()))
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.report.error.exempt")))

        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.report.success"), TagResolver.resolver(
            Placeholder.parsed("player", target.name.toString()),
            Placeholder.parsed("category", category.name)
        )))

        magenta.serverFeedback.client.send(magenta.serverFeedback.addEmbed {
            setTitle(WebhookEmbed.EmbedTitle("Nahlášen hráč ${target.name.toString()} (#${magenta.random})", null))
            setColor(0xde4343)
            setDescription(category.message)
            setThumbnailUrl(avatar.format(trimUUID(target.uniqueId)))
            addField(WebhookEmbed.EmbedField(true, "Nahlásil", player.name))
            addField(WebhookEmbed.EmbedField(true, "Důvod", category.name))
            addField(WebhookEmbed.EmbedField(false, "Zpráva", message))
            setFooter(WebhookEmbed.EmbedFooter("Byl nahlášen ${now()}", null))
        })
    }

}