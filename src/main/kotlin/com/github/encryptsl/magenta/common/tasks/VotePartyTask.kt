package com.github.encryptsl.magenta.common.tasks

import club.minnced.discord.webhook.send.WebhookEmbed
import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.kmono.lib.extensions.toMinecraftAvatar
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.vote.VotePartyPlayerWinner
import com.github.encryptsl.magenta.common.hook.nuvotifier.VoteHelper.broadcast
import com.github.encryptsl.magenta.common.hook.nuvotifier.VoteHelper.giveRewards
import com.github.encryptsl.magenta.common.utils.ActionTitleManager
import com.tcoded.folialib.wrapper.task.WrappedTask
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Bukkit
import java.util.function.Consumer

class VotePartyTask(private val magenta: Magenta, private val commands: MutableList<String>) : Consumer<WrappedTask> {

    private var timer: Int = magenta.config.getInt("votifier.voteparty.countdown")

    override fun accept(t: WrappedTask) {
        if (magenta.config.getIntegerList("votifier.voteparty.countdown_at").contains(timer)) {
            ActionTitleManager.sendTitleAndSubtitle(
                Audience.audience(Bukkit.getOnlinePlayers()),
                ModernText.miniModernText("<blue>VoteParty"),
                magenta.locale.translation("magenta.votifier.voteparty.broadcast",
                    Placeholder.parsed("delay", timer.toString())
                ), 0, magenta.config.getInt("votifier.voteparty.countdown").toLong(), 1
            )
        }

        if (timer == 0) {
            val players = Bukkit.getOnlinePlayers()
            giveRewards(players, commands)
            if (magenta.config.contains("votifier.voteparty.random")) {
                val player = players.random()
                giveRewards(magenta.config.getStringList("votifier.voteparty.random"), player.name)
                magenta.voteParty.partyFinished(player.name)
                magenta.pluginManager.callEvent(VotePartyPlayerWinner(player.name))
                try {
                    magenta.notification.addEmbed {
                        setTitle(WebhookEmbed.EmbedTitle("VoteParty", null))
                        setThumbnailUrl(player.uniqueId.toMinecraftAvatar())
                        setColor(0xa730c2)
                        addField(WebhookEmbed.EmbedField(false, "Výherce", player.name))
                    }?.let { magenta.notification.client.send(it) }
                } catch (_ : Exception) { }
            }
            t.cancel()
            broadcast(magenta.locale.translation("magenta.votifier.voteparty.success"))
        }
        timer--
    }

}