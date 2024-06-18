package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.kmono.lib.api.commands.AnnotationFeatures
import com.github.encryptsl.kmono.lib.utils.pagination.ComponentPaginator
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.modules.milestones.VoteMilestonesGUI
import com.github.encryptsl.magenta.common.extensions.positionIndexed
import com.github.encryptsl.magenta.common.hook.nuvotifier.VoteHelper
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.*
import org.incendo.cloud.paper.LegacyPaperCommandManager

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class VoteCmd(val magenta: Magenta) : AnnotationFeatures {

    private val voteMilestonesGUI: VoteMilestonesGUI by lazy { VoteMilestonesGUI(magenta) }

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: LegacyPaperCommandManager<CommandSender>
    ) {
        annotationParser.parse(this)
    }

    @Command("vote")
    @Permission("magenta.vote")
    @CommandDescription("This command send vote links and your votes")
    fun onVote(player: Player) {
        val user = magenta.user.getUser(player.uniqueId)

        val services = magenta.config.getConfigurationSection("votifier.services")?.getKeys(false) ?: return

        for (service in services) {
            if(service.contains("default")) continue
            val replace = VoteHelper.replaceService(service, "_", ".")
            player.sendMessage(ModernText.miniModernText(magenta.config.getString("votifier.services.$service.link").toString(), TagResolver.resolver(
                Placeholder.component("hover", magenta.locale.translation("magenta.command.vote.hover")),
                Placeholder.parsed("vote", (user.getVotesByService(replace)).toString()),
                Placeholder.parsed("username", player.name)
            )))
        }
    }

    @Command("vote milestones")
    @Permission("magenta.vote.milestones")
    @CommandDescription("This command open vote milestones")
    fun onVoteMilestones(player: Player) {
        voteMilestonesGUI.open(player)
    }

    @Command("vote top [page]")
    @Permission("magenta.vote.top")
    @CommandDescription("This command shows leaderboards in vote")
    fun onVoteLeaderBoard(commandSender: CommandSender, @Argument(value = "page", description = "page of leaderboard") @Default("1") page: Int) {
        commandSender.sendMessage(magenta.locale.translation("magenta.command.vote.top.header"))

        val leaderBoard = magenta.vote.votesLeaderBoard().toList().positionIndexed { k, v ->
            magenta.locale.translation("magenta.command.vote.top", TagResolver.resolver(
                Placeholder.parsed("position", k.toString()),
                Placeholder.parsed("player", v.first),
                Placeholder.parsed("votes", v.second.toString())
            )).appendNewline().append(magenta.locale.translation("magenta.command.vote.top.footer"))
        }

        if (leaderBoard.isEmpty()) return
        
        val paginator = ComponentPaginator(leaderBoard).apply { page(page) }

        if (paginator.isAboveMaxPage(page))
            return commandSender.sendMessage(magenta.locale.translation("magenta.pagination.error.maximum.pages",
                Placeholder.parsed("max_page", paginator.maxPages.toString())
            ))

        for (component in paginator.display()) {
            commandSender.sendMessage(component)
        }
    }

    @Command("vote claim rewards")
    @Permission("magenta.vote.claim.rewards")
    @CommandDescription("This command claim your rewards from voting")
    fun onVoteClaimRewards(player: Player) {
        val user = magenta.user.getUser(player.uniqueId)
        if (!user.getAccount().contains("votifier.rewards"))
            return player.sendMessage(magenta.locale.translation("magenta.command.vote.error.not.reward"))

        VoteHelper.giveRewards(user.getVotifierRewards(), player.name)
        player.sendMessage(magenta.locale.translation("magenta.command.vote.success.claim.rewards"))
        user.set("votifier.rewards", null)
    }

    @Command("voteparty|vparty")
    @Permission("magenta.voteparty")
    @CommandDescription("This command send information about vote party")
    fun onVoteParty(commandSender: CommandSender) {
        val startAt = magenta.config.getInt("votifier.voteparty.start_at")

        if (!magenta.config.getBoolean("votifier.voteparty.enabled"))
            return commandSender.sendMessage(magenta.locale.translation("magenta.command.voteparty.error"))

        val format = magenta.config.getString("votifier.voteparty.format") ?: return

        magenta.voteParty.getVoteParty().thenApply { voteParty ->
            commandSender.sendMessage(ModernText.miniModernText(format, TagResolver.resolver(
                Placeholder.parsed("remaining_votes", startAt.minus(voteParty.currentVotes).toString()),
                Placeholder.parsed("current_votes", voteParty.currentVotes.toString()),
                Placeholder.parsed("start_at", startAt.toString()),
                Placeholder.parsed("last_party", voteParty.lastVoteParty.toString()),
                Placeholder.parsed("last_winner", voteParty.lastWinnerOfParty ?: "Nobody"),
            )))
        }.exceptionally { ex ->
            commandSender.sendMessage(magenta.locale.translation("magenta.exception", Placeholder.parsed("exception", ex.message ?: ex.localizedMessage)))
            magenta.logger.severe(ex.message ?: ex.localizedMessage)
        }
    }
}