package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.account.PlayerAccount
import com.github.encryptsl.magenta.common.hook.nuvotifier.VoteHelper
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class VoteCmd(val magenta: Magenta) {
    @CommandMethod("vote")
    @CommandPermission("magenta.vote")
    fun onVote(player: Player) {
        val services: List<String> = magenta.config.getConfigurationSection("votifier.services")?.getKeys(false)?.filter { service -> !service.contains("default") } ?: return
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
        val playerAccount = PlayerAccount(magenta, player.uniqueId)
        if (playerAccount.getAccount().contains("votifier.rewards")) {
            magenta.schedulerMagenta.runTask(magenta) {
                VoteHelper.giveRewards(playerAccount.getVotifierRewards(), player.name)
            }
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.vote.success.claim.rewards")))
            playerAccount.set("votifier.rewards", null)
        } else {
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.vote.error.not.reward")))
        }
    }
}