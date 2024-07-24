package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.kmono.lib.api.commands.AnnotationFeatures
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.annotation.specifier.Range
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.annotations.*
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.paper.LegacyPaperCommandManager
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.suggestion.Suggestion
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.extensions.sendConsoleCommand
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class RandomCmd(private val magenta: Magenta) : AnnotationFeatures {

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: LegacyPaperCommandManager<CommandSender>
    ) {
        commandManager.parserRegistry().registerSuggestionProvider("tags") {_, _ ->
            return@registerSuggestionProvider CompletableFuture.completedFuture(
                magenta.randomConfig.getConfig().getConfigurationSection("tags")
                    ?.getKeys(false)
                    ?.mapNotNull { Suggestion.suggestion(it.toString()) }!!
            )
        }
        annotationParser.parse(this)
    }

    @Command("random world tickets <player> [amount]")
    @Permission("magenta.random.world.tickets")
    @CommandDescription("This command give random world ticket to player")
    fun onRandomTickets(
        commandSender: CommandSender,
        @Argument(value = "player", suggestions = "players") target: Player,
        @Argument("amount") @Range(min = "1", max = "100") amount: Int = 1
    ) {
        val tickets: List<String> = magenta.randomConfig.getConfig().getStringList("world_tickets.tickets")
        val randomKey = tickets.random().replace("%amount%", amount.toString())
        magenta.logger.info("Hráč ${target.name} dostal náhodnou vstupenku !")

        sendConsoleCommand(randomKey, target)

        target.sendMessage(magenta.locale.translation("magenta.command.random.world.ticket.success.player",
            Placeholder.parsed("amount", amount.toString())
        ))
        commandSender.sendMessage(magenta.locale.translation("magenta.command.random.world.ticket.success.self.other", TagResolver.resolver(
            Placeholder.parsed("player", target.name),
            Placeholder.parsed("amount", amount.toString())
        )))
    }

    @Command("random tag <type> <player>")
    @Permission("magenta.random.tag")
    @CommandDescription("This command give random tag to player")
    fun onRandomTag(
        commandSender: CommandSender,
        @Argument(value = "type", suggestions = "tags") type: String,
        @Argument(value = "player", suggestions = "players") target: Player
    ) {
        val tags: List<String> = magenta.randomConfig.getConfig().getStringList("tags.$type")
        val randomTag = tags.random()
        if (!target.hasPermission(randomTag)) {
            sendConsoleCommand("lp user %player% permission set $randomTag", target)
            return target.sendMessage(magenta.locale.translation("magenta.command.random.tag.success.player",
                Placeholder.parsed("category", type)
            ))
        }

        magenta.logger.info("Hráč ${target.name} již $randomTag oprávnění vlastní proto mu byl nabídnut jiný tag !")
        sendConsoleCommand("lp user %player% permission set $randomTag", target)
        target.sendMessage(magenta.locale.translation("magenta.command.random.tag.success.player",
            Placeholder.parsed("category", type)
        ))
        commandSender.sendMessage(magenta.locale.translation("magenta.command.random.tag.success.self.other", TagResolver.resolver(
            Placeholder.parsed("category", type),
            Placeholder.parsed("tag", randomTag)
        )))
    }
}