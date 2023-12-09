package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.command.CommandSender

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class VotePartyCmd(private val magenta: Magenta) {
    @CommandMethod("voteparty|vparty")
    @CommandPermission("magenta.voteparty")
    fun onVoteParty(commandSender: CommandSender) {
        val (currentVotes, lastParty, lastWinner) = magenta.voteParty.getVoteParty()
        val startAt = magenta.config.getInt("votifier.voteparty.start_at")

        if (!magenta.config.getBoolean("votifier.voteparty.enabled"))
            return commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.voteparty.error")))

        magenta.config.getStringList("votifier.voteparty.format").forEach { message ->
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