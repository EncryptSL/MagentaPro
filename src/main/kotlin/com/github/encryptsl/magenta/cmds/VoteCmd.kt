package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.database.entity.VoteEntity
import com.github.encryptsl.magenta.common.hook.nuvotifier.VoteHelper
import com.github.encryptsl.magenta.common.utils.ModernText
import kotlinx.datetime.Instant
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class VoteCmd(val magenta: Magenta) {
    @CommandMethod("vote")
    @CommandPermission("magenta.vote")
    fun onVote(player: Player) {
        val services: List<String> = magenta.config.getConfigurationSection("votifier.services")?.getKeys(false)
            ?.filter { service -> !service.contains("default") } ?: return
        services.forEach { service ->
            val replace = VoteHelper.replaceService(service, "_", ".")
            magenta.schedulerMagenta.delayedTask(magenta, {
                player.sendMessage(ModernText.miniModernText(magenta.config.getString("votifier.services.$service.link").toString(), TagResolver.resolver(
                    Placeholder.parsed("hover", magenta.localeConfig.getMessage("magenta.command.vote.hover")),
                    Placeholder.parsed("vote", (magenta.vote.getPlayerVote(player.uniqueId, replace)?.vote ?: "0").toString()),
                    Placeholder.parsed("username", player.name)
                )))
            }, 10)
        }
    }

    @CommandMethod("vote claim rewards")
    @CommandPermission("magenta.vote.claim.rewards")
    fun onVoteClaimRewards(player: Player) {
        val user = magenta.user.getUser(player.uniqueId)
        if (!user.getAccount().contains("votifier.rewards"))
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.vote.error.not.reward")))

        magenta.schedulerMagenta.doSync(magenta) {
            VoteHelper.giveRewards(user.getVotifierRewards(), player.name)
        }
        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.vote.success.claim.rewards")))
        user.set("votifier.rewards", null)
    }

    @CommandMethod("vote admin reset <player>")
    fun onVoteAdminReset(
        commandSender: CommandSender,
        @Argument(value = "player", suggestions = "offlinePlayers") target: OfflinePlayer
    ) {
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.vote.admin.reset"),
            Placeholder.parsed("player", target.name.toString())
        ))
    }

    @CommandMethod("vote admin add vote <service> <player> <amount>")
    @CommandPermission("magenta.vote.admin.add")
    fun onVoteAdminAdd(
        commandSender: CommandSender,
        @Argument(value = "service", suggestions = "services") service: String,
        @Argument(value = "player", suggestions = "offlinePlayers") target: OfflinePlayer,
        @Argument(value = "amount") amount: Int
    ) {
        if (!magenta.vote.hasAccount(target.uniqueId, service))
            return commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.vote.error.not.player.exist"), TagResolver.resolver(
                Placeholder.parsed("player", target.name.toString()),
                Placeholder.parsed("service", service)
            )))
        magenta.vote.addVote(VoteEntity(target.name.toString(), target.uniqueId, amount, service, Instant.fromEpochMilliseconds(System.currentTimeMillis())))
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.vote.admin.add"), TagResolver.resolver(
            Placeholder.parsed("service", service),
            Placeholder.parsed("player", target.name.toString()),
            Placeholder.parsed("amount", amount.toString())
        )))
    }

    @CommandMethod("vote admin set <service> <player> <amount>")
    @CommandPermission("magenta.vote.admin.set")
    fun onVoteAdminSet(
        commandSender: CommandSender,
        @Argument(value = "service", suggestions = "services") service: String,
        @Argument(value = "player", suggestions = "offlinePlayers") target: OfflinePlayer,
        @Argument(value = "amount") amount: Int
    ) {
        if (!magenta.vote.hasAccount(target.uniqueId, service))
            return commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.vote.error.player.not.exist"), TagResolver.resolver(
                Placeholder.parsed("player", target.name.toString()),
                Placeholder.parsed("service", service)
            )))
        magenta.vote.setVote(VoteEntity(target.name.toString(), target.uniqueId, amount, service, Instant.fromEpochMilliseconds(System.currentTimeMillis())))
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.vote.admin.set"), TagResolver.resolver(
            Placeholder.parsed("service", service),
            Placeholder.parsed("player", target.name.toString()),
            Placeholder.parsed("amount", amount.toString())
        )))
    }
    @CommandMethod("vote admin remove vote <service> <player> <amount>")
    @CommandPermission("magenta.vote.admin.remove")
    fun onVoteAdminRemove(
        commandSender: CommandSender,
        @Argument(value = "service", suggestions = "services") service: String,
        @Argument(value = "player", suggestions = "offlinePlayers") target: OfflinePlayer,
        @Argument(value = "amount") amount: Int
    ) {
        if (!magenta.vote.hasAccount(target.uniqueId, service))
            return commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.vote.error.player.not.exist"), TagResolver.resolver(
                Placeholder.parsed("player", target.name.toString()),
                Placeholder.parsed("service", service)
            )))

        magenta.vote.removeVote(VoteEntity(target.name.toString(), target.uniqueId, amount, service, Instant.fromEpochMilliseconds(System.currentTimeMillis())))
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.vote.admin.remove"), TagResolver.resolver(
            Placeholder.parsed("service", service),
            Placeholder.parsed("player", target.name.toString()),
            Placeholder.parsed("amount", amount.toString())
        )))
    }
}