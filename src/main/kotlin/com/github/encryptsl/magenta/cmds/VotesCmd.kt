package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.kmono.lib.api.commands.AnnotationFeatures
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.annotations.*
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.paper.LegacyPaperCommandManager
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.suggestion.Suggestion
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.database.entity.VoteEntity
import com.github.encryptsl.magenta.common.hook.nuvotifier.VoteHelper
import kotlinx.datetime.Instant
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import java.util.concurrent.CompletableFuture

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class VotesCmd(private val magenta: Magenta) : AnnotationFeatures {

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: LegacyPaperCommandManager<CommandSender>
    ) {
        commandManager.parserRegistry().registerSuggestionProvider("services") {_, _ ->
            return@registerSuggestionProvider CompletableFuture.completedFuture(
                magenta.config.getConfigurationSection("votifier.services")
                    ?.getKeys(false)
                    ?.mapNotNull { Suggestion.suggestion(VoteHelper.replaceService(it.toString(), "_", ".")) }!!
            )
        }
        annotationParser.parse(this)
    }

    @Command("votes add <service> <player> <amount>")
    @Permission("magenta.votes.add")
    @CommandDescription("This command add vote to service where player voted")
    fun onVotesAdd(
        commandSender: CommandSender,
        @Argument(value = "service", suggestions = "services") service: String,
        @Argument(value = "player", suggestions = "offlinePlayers") target: OfflinePlayer,
        @Argument(value = "amount") amount: Int
    ) {
        magenta.vote.getUserVotesByUUIDAndService(target.uniqueId, service).thenApply {
            magenta.vote.addVote(VoteEntity(target.name.toString(), target.uniqueId, amount, service, Instant.fromEpochMilliseconds(System.currentTimeMillis())))
            commandSender.sendMessage(
                magenta.locale.translation("magenta.command.votes.success.add", TagResolver.resolver(
                    Placeholder.parsed("service", service),
                    Placeholder.parsed("player", target.name.toString()),
                    Placeholder.parsed("amount", amount.toString())
                )))
        }.exceptionally {
            commandSender.sendMessage(magenta.locale.translation("magenta.command.vote.error.not.player.exist", TagResolver.resolver(
                    Placeholder.parsed("player", target.name.toString()),
                    Placeholder.parsed("service", service)
            )))
        }
    }

    @Command("votes set <service> <player> <amount>")
    @Permission("magenta.votes.set")
    @CommandDescription("This command set vote to service where player voted")
    fun onVotesSet(
        commandSender: CommandSender,
        @Argument(value = "service", suggestions = "services") service: String,
        @Argument(value = "player", suggestions = "offlinePlayers") target: OfflinePlayer,
        @Argument(value = "amount") amount: Int
    ) {
        magenta.vote.getUserVotesByUUIDAndService(target.uniqueId, service).thenApply {
            magenta.vote.setVote(target.uniqueId, service, amount)
            commandSender.sendMessage(
                magenta.locale.translation("magenta.command.votes.success.set", TagResolver.resolver(
                    Placeholder.parsed("service", service),
                    Placeholder.parsed("player", target.name.toString()),
                    Placeholder.parsed("amount", amount.toString())
                )))
        }.exceptionally {
            commandSender.sendMessage(
                magenta.locale.translation("magenta.command.vote.error.not.player.exist", TagResolver.resolver(
                    Placeholder.parsed("player", target.name.toString()),
                    Placeholder.parsed("service", service)
                )))
        }
    }

    @Command("votes reset player <player>")
    @Permission("magenta.votes.reset.player")
    @CommandDescription("This command reset player votes")
    fun onVotesReset(
        commandSender: CommandSender,
        @Argument(value = "player", suggestions = "offlinePlayers") target: OfflinePlayer
    ) {
        magenta.vote.getUserVotesByUUID(target.uniqueId).thenApply {
            magenta.vote.resetVotes(target.uniqueId)
            commandSender.sendMessage(
                magenta.locale.translation("magenta.command.votes.success.reset",
                    Placeholder.parsed("player", target.name.toString())
                ))
        }.exceptionally {
            commandSender.sendMessage(
                magenta.locale.translation("magenta.command.vote.error.not.player.exist", TagResolver.resolver(
                    Placeholder.parsed("player", target.name.toString()),
                    Placeholder.parsed("service", "")
                )))
        }
    }

    @Command("votes reset all")
    @Permission("magenta.votes.reset.all")
    @CommandDescription("This command reset all votes to zero")
    fun onVotesResetAll(
        commandSender: CommandSender
    ) {
        magenta.vote.resetVotes()
        commandSender.sendMessage(magenta.locale.translation("magenta.command.votes.success.reset.all"))
    }
    @Command("votes remove vote <service> <player> <amount>")
    @Permission("magenta.votes.remove")
    @CommandDescription("This command remove vote from service where player vote")
    fun onVotesRemove(
        commandSender: CommandSender,
        @Argument(value = "service", suggestions = "services") service: String,
        @Argument(value = "player", suggestions = "offlinePlayers") target: OfflinePlayer,
        @Argument(value = "amount") amount: Int
    ) {
        magenta.vote.getUserVotesByUUIDAndService(target.uniqueId, service).thenApply {
            magenta.vote.removeVote(target.uniqueId, service, amount)
            commandSender.sendMessage(
                magenta.locale.translation("magenta.command.votes.success.remove", TagResolver.resolver(
                    Placeholder.parsed("service", service),
                    Placeholder.parsed("player", target.name.toString()),
                    Placeholder.parsed("amount", amount.toString())
                )))
        }.exceptionally {
            commandSender.sendMessage(
                magenta.locale.translation("magenta.command.vote.error.player.not.exist", TagResolver.resolver(
                    Placeholder.parsed("player", target.name.toString()),
                    Placeholder.parsed("service", service)
                )))
        }
    }

    @Command("votes remove all")
    @Permission("magenta.votes.remove.all")
    @CommandDescription("This command remove all vote data")
    fun onVotesDeleteAll(commandSender: CommandSender) {
        magenta.vote.deleteAll()
        commandSender.sendMessage(magenta.locale.translation("magenta.command.votes.success.remove.all"))
    }

}