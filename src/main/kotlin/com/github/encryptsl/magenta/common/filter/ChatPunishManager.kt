package com.github.encryptsl.magenta.common.filter

import club.minnced.discord.webhook.send.WebhookEmbed
import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.extensions.now
import com.github.encryptsl.magenta.common.extensions.toMinotarAvatar
import com.github.encryptsl.magenta.common.filter.impl.ChatFilters
import fr.euphyllia.energie.model.SchedulerType
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

class ChatPunishManager(private val magenta: Magenta) {

    private val flagging = HashMap<UUID, Int>()

    private fun kick(player: Player, actions: List<String>, score: Int, filterName: String) {
        if (actions.contains("kick")) {
            if (score >= 2) {
                Magenta.scheduler.runTask(SchedulerType.SYNC) {
                    flagging.remove(player.uniqueId)
                    player.kick(magenta.locale.translation("magenta.filter.action.kick", Placeholder.parsed("reason", filterName)))
                }
            }
        }
    }

    private fun notify(player: Player, actions: List<String>, score: Int, filterName: String, phrase: String) {
        if (actions.contains("notify")) {
            if (score == 1) {
                magenta.serverFeedback.addEmbed {
                    setAuthor(WebhookEmbed.EmbedAuthor("Chat Filter 1.0.0 - Varování", null, null))
                    setThumbnailUrl(player.uniqueId.toMinotarAvatar())
                    setDescription("Se pokusil napsat něco co je zakázáno !")
                    addField(WebhookEmbed.EmbedField(true, "Detekován Hráč", player.name))
                    addField(WebhookEmbed.EmbedField(true, "Detekoval filter", filterName))
                    addField(WebhookEmbed.EmbedField(false, "Napsal", phrase))
                    setFooter(WebhookEmbed.EmbedFooter("Detekováno ${now()}", null))
                }?.let { magenta.serverFeedback.client.send(it) }
            }
            Bukkit.broadcast(magenta.locale.translation("magenta.filter.admin.notify", TagResolver.resolver(
                Placeholder.parsed("player", player.name),
                Placeholder.parsed("flagged", filterName),
                Placeholder.parsed("msg", phrase))),
                "magenta.filter.notify"
            )
        }
    }

    private fun message(player: Player, actions: List<String>, translation: String?) {
        if (actions.contains("message")) {
            translation?.let { player.sendMessage(ModernText.miniModernText(translation, Placeholder.parsed("player", player.name))) }
        }
    }

    private fun cancel(actions: List<String>, event: AsyncChatEvent) {
        if (actions.contains("cancel")) {
            event.isCancelled = true
        }
    }

    fun action(player: Player, event: AsyncChatEvent, translation: String?, messageFromChat: String, violations: ChatFilters) {
        val actionList = magenta.chatControl.getConfig().getStringList("filters.${violations.name.lowercase()}.action")
        if (actionList.contains("none")) return

        flagging.putIfAbsent(player.uniqueId, 0)
        flagging.computeIfPresent(player.uniqueId) { _, i -> i + 1 } ?: 1
        val score = flagging[player.uniqueId] ?: 0

        message(player, actionList, translation)
        if (score == 1) {
            notify(player, actionList, score, violations.name, messageFromChat)
        } else if (score == 3) {
            kick(player, actionList, score, violations.name)
        }
        cancel(actionList, event)
    }
}