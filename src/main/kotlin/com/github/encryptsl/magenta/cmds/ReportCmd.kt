package com.github.encryptsl.magenta.cmds

import club.minnced.discord.webhook.send.WebhookEmbed
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.commands.AnnotationFeatures
import com.github.encryptsl.magenta.api.report.ReportCategories
import com.github.encryptsl.magenta.common.extensions.now
import com.github.encryptsl.magenta.common.extensions.toMinotarAvatar
import com.github.encryptsl.magenta.common.hook.luckperms.LuckPermsAPI
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotation.specifier.Greedy
import org.incendo.cloud.annotations.*
import org.incendo.cloud.paper.PaperCommandManager
import org.incendo.cloud.suggestion.Suggestion
import java.util.concurrent.CompletableFuture

@Suppress("UNUSED")
class ReportCmd(private val magenta: Magenta) : AnnotationFeatures {

    private val luckPermsAPI: LuckPermsAPI by lazy { LuckPermsAPI() }

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: PaperCommandManager<CommandSender>
    ) {
        commandManager.parserRegistry().registerSuggestionProvider("reportCategories") {_, _ ->
            return@registerSuggestionProvider CompletableFuture
                .completedFuture(ReportCategories.entries.map { Suggestion.suggestion(it.name) })
        }
        annotationParser.parse(this)
    }

    @Command("report <player> <category> [message]")
    @Permission("magenta.report")
    @CommandDescription("This command send report to administrators")
    fun onReport(
        player: Player,
        @Argument(value = "player", suggestions = "offlinePlayers") target: OfflinePlayer,
        @Argument(value = "category", suggestions = "reportCategories") category: ReportCategories,
        @Argument(value = "message") @Default("Zpráva není specifikovaná.") @Greedy message: String
    ) {
        if (player.name.equals(target.name, ignoreCase = true))
            return player.sendMessage(magenta.locale.translation("magenta.command.report.error.yourself"))

        if (magenta.stringUtils.inInList("exempt-blacklist", target.name.toString()) || luckPermsAPI.hasPermission(target, "magenta.report.exempt"))
            return player.sendMessage(magenta.locale.translation("magenta.command.report.error.exempt"))

        player.sendMessage(magenta.locale.translation("magenta.command.report.success", TagResolver.resolver(
            Placeholder.parsed("player", target.name.toString()),
            Placeholder.parsed("category", category.name)
        )))

        magenta.serverFeedback.addEmbed {
            setTitle(WebhookEmbed.EmbedTitle("Nahlášen hráč ${target.name.toString()} (#${magenta.random})", null))
            setColor(0xde4343)
            setDescription(category.message)
            setThumbnailUrl(target.uniqueId.toMinotarAvatar())
            addField(WebhookEmbed.EmbedField(true, "Nahlásil", player.name))
            addField(WebhookEmbed.EmbedField(true, "Důvod", category.name))
            addField(WebhookEmbed.EmbedField(false, "Zpráva", message))
            setFooter(WebhookEmbed.EmbedFooter("Byl nahlášen ${now()}", null))
        }?.let { magenta.serverFeedback.client.send(it) }
    }

}