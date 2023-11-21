package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.database.entity.VoteEntity
import com.github.encryptsl.magenta.common.utils.ModernText
import kotlinx.datetime.Instant
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class VotesCmd(private val magenta: Magenta) {

    @CommandMethod("votes add <service> <player> <amount>")
    @CommandPermission("magenta.votes.add")
    fun onVotesAdd(
        commandSender: CommandSender,
        @Argument(value = "service", suggestions = "services") service: String,
        @Argument(value = "player", suggestions = "offlinePlayers") target: OfflinePlayer,
        @Argument(value = "amount") amount: Int
    ) {
        if (!magenta.vote.hasAccount(target.uniqueId, service))
            return commandSender.sendMessage(
                ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.vote.error.not.player.exist"), TagResolver.resolver(
                Placeholder.parsed("player", target.name.toString()),
                Placeholder.parsed("service", service)
            )))
        magenta.vote.addVote(VoteEntity(target.name.toString(), target.uniqueId, amount, service, Instant.fromEpochMilliseconds(System.currentTimeMillis())))
        commandSender.sendMessage(
            ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.votes.success.add"), TagResolver.resolver(
            Placeholder.parsed("service", service),
            Placeholder.parsed("player", target.name.toString()),
            Placeholder.parsed("amount", amount.toString())
        )))
    }

    @CommandMethod("votes set <service> <player> <amount>")
    @CommandPermission("magenta.votes.set")
    fun onVotesSet(
        commandSender: CommandSender,
        @Argument(value = "service", suggestions = "services") service: String,
        @Argument(value = "player", suggestions = "offlinePlayers") target: OfflinePlayer,
        @Argument(value = "amount") amount: Int
    ) {
        if (!magenta.vote.hasAccount(target.uniqueId, service))
            return commandSender.sendMessage(
                ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.vote.error.not.player.exist"), TagResolver.resolver(
                    Placeholder.parsed("player", target.name.toString()),
                    Placeholder.parsed("service", service)
                )))
        magenta.vote.setVote(target.uniqueId, service, amount)
        commandSender.sendMessage(
            ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.votes.success.set"), TagResolver.resolver(
                Placeholder.parsed("service", service),
                Placeholder.parsed("player", target.name.toString()),
                Placeholder.parsed("amount", amount.toString())
            )))
    }

    @CommandMethod("votes reset player <player>")
    @CommandPermission("magenta.votes.reset.player")
    fun onVotesReset(
        commandSender: CommandSender,
        @Argument(value = "player", suggestions = "offlinePlayers") target: OfflinePlayer
    ) {

        if (magenta.vote.hasAccount(target.uniqueId))
            return commandSender.sendMessage(
                ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.vote.error.not.player.exist"), TagResolver.resolver(
                    Placeholder.parsed("player", target.name.toString()),
                    Placeholder.parsed("service", "")
                )))

        magenta.vote.resetVotes(target.uniqueId)
        commandSender.sendMessage(
            ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.votes.success.reset"),
                Placeholder.parsed("player", target.name.toString())
            ))
    }

    @CommandMethod("votes reset all")
    @CommandPermission("magenta.votes.reset.all")
    fun onVotesResetAll(
        commandSender: CommandSender
    ) {
        magenta.vote.resetVotes()
        commandSender.sendMessage(
            ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.votes.success.reset.all")))
    }
    @CommandMethod("votes remove vote <service> <player> <amount>")
    @CommandPermission("magenta.votes.remove")
    fun onVotesRemove(
        commandSender: CommandSender,
        @Argument(value = "service", suggestions = "services") service: String,
        @Argument(value = "player", suggestions = "offlinePlayers") target: OfflinePlayer,
        @Argument(value = "amount") amount: Int
    ) {
        if (!magenta.vote.hasAccount(target.uniqueId, service))
            return commandSender.sendMessage(
                ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.vote.error.player.not.exist"), TagResolver.resolver(
                Placeholder.parsed("player", target.name.toString()),
                Placeholder.parsed("service", service)
            )))

        magenta.vote.removeVote(target.uniqueId, service, amount)
        commandSender.sendMessage(
            ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.votes.success.remove"), TagResolver.resolver(
            Placeholder.parsed("service", service),
            Placeholder.parsed("player", target.name.toString()),
            Placeholder.parsed("amount", amount.toString())
        )))
    }

    @CommandMethod("votes remove all")
    @CommandPermission("magenta.votes.remove.all")
    fun onVotesDeleteAll(commandSender: CommandSender) {
        magenta.vote.deleteAll()
        commandSender.sendMessage(
            ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.votes.success.remove.all"))
        )
    }

}