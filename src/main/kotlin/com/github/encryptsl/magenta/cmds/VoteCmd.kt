package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.scheduler.SchedulerMagenta
import com.github.encryptsl.magenta.common.hook.nuvotifier.VoteHelper
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class VoteCmd(val magenta: Magenta) {
    @Command("vote")
    @Permission("magenta.vote")
    fun onVote(player: Player) {
        val user = magenta.user.getUser(player.uniqueId)

        val services: List<String> = magenta.config.getConfigurationSection("votifier.services")?.getKeys(false)
            ?.filter { service -> !service.contains("default") } ?: return
        services.forEach { service ->
            val replace = VoteHelper.replaceService(service, "_", ".")
            SchedulerMagenta.delayedTask(magenta, {
                player.sendMessage(ModernText.miniModernText(magenta.config.getString("votifier.services.$service.link").toString(), TagResolver.resolver(
                    Placeholder.parsed("hover", magenta.localeConfig.getMessage("magenta.command.vote.hover")),
                    Placeholder.parsed("vote", (user.getVotesByService(replace)).toString()),
                    Placeholder.parsed("username", player.name)
                )))
            }, 10)
        }
    }

    @Command("vote claim rewards")
    @Permission("magenta.vote.claim.rewards")
    fun onVoteClaimRewards(player: Player) {
        val user = magenta.user.getUser(player.uniqueId)
        if (!user.getAccount().contains("votifier.rewards"))
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.vote.error.not.reward")))

        SchedulerMagenta.doSync(magenta) {
            VoteHelper.giveRewards(user.getVotifierRewards(), player.name)
        }
        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.vote.success.claim.rewards")))
        user.set("votifier.rewards", null)
    }
}