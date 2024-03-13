package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.vote.VoteMilestonesGUI
import com.github.encryptsl.magenta.common.hook.nuvotifier.VoteHelper
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class VoteCmd(val magenta: Magenta) {

    private val voteMilestonesGUI: VoteMilestonesGUI by lazy { VoteMilestonesGUI(magenta) }

    @Command("vote")
    @Permission("magenta.vote")
    fun onVote(player: Player) {
        val user = magenta.user.getUser(player.uniqueId)

        val services = magenta.config.getConfigurationSection("votifier.services")?.getKeys(false) ?: return

        for (service in services) {
            if(service.contains("default")) continue
            val replace = VoteHelper.replaceService(service, "_", ".")
            player.sendMessage(ModernText.miniModernText(magenta.config.getString("votifier.services.$service.link").toString(), TagResolver.resolver(
                Placeholder.parsed("hover", magenta.localeConfig.getMessage("magenta.command.vote.hover")),
                Placeholder.parsed("vote", (user.getVotesByService(replace)).toString()),
                Placeholder.parsed("username", player.name)
            )))
        }
    }

    @Command("vote milestones")
    @Permission("magenta.vote.milestones")
    fun onVoteMilestones(player: Player) {
        voteMilestonesGUI.openVoteMilestonesGui(player)
    }

    @Command("vote claim rewards")
    @Permission("magenta.vote.claim.rewards")
    fun onVoteClaimRewards(player: Player) {
        val user = magenta.user.getUser(player.uniqueId)
        if (!user.getAccount().contains("votifier.rewards"))
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.vote.error.not.reward")))

        VoteHelper.giveRewards(user.getVotifierRewards(), player.name)
        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.vote.success.claim.rewards")))
        user.set("votifier.rewards", null)
    }

    @Command("voteparty|vparty")
    @Permission("magenta.voteparty")
    fun onVoteParty(commandSender: CommandSender) {
        val (currentVotes, lastParty, lastWinner) = magenta.voteParty.getVoteParty()
        val startAt = magenta.config.getInt("votifier.voteparty.start_at")

        if (!magenta.config.getBoolean("votifier.voteparty.enabled"))
            return commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.voteparty.error")))

        val format = magenta.config.getStringList("votifier.voteparty.format")

        for (message in format) {
            commandSender.sendMessage(ModernText.miniModernText(message, TagResolver.resolver(
                Placeholder.parsed("remaining_votes", startAt.minus(currentVotes).toString()),
                Placeholder.parsed("current_votes", currentVotes.toString()),
                Placeholder.parsed("start_at", startAt.toString()),
                Placeholder.parsed("last_party", lastParty.toString()),
                Placeholder.parsed("last_winner", lastWinner ?: "Nobody"),
            )))
        }
    }
}