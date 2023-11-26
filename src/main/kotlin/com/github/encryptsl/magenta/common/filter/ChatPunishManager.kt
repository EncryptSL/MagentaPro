package com.github.encryptsl.magenta.common.filter

import club.minnced.discord.webhook.send.WebhookEmbed
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.chat.enums.Violations
import com.github.encryptsl.magenta.common.extensions.avatar
import com.github.encryptsl.magenta.common.extensions.now
import com.github.encryptsl.magenta.common.extensions.trimUUID
import com.github.encryptsl.magenta.common.utils.ModernText
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

class ChatPunishManager(private val magenta: Magenta) {

    private val flagging = HashMap<UUID, Int>()

    fun action(player: Player, event: AsyncChatEvent, translation: String?, messageFromChat: String, violations: Violations) {
        val actionList = magenta.chatControl.getConfig().getStringList("filters.${violations.name.lowercase()}.action")
        if (actionList.contains("none")) return

        if (actionList.contains("kick")) {
            flagging.putIfAbsent(player.uniqueId, 0)
            flagging.computeIfPresent(player.uniqueId) { _, i -> i + 1 } ?: 1
            val score = flagging[player.uniqueId] ?: 0
            if (score >= 2) {
                magenta.schedulerMagenta.doSync(magenta) {
                    player.kick(
                        ModernText.miniModernText(
                            magenta.localeConfig.getMessage("magenta.filter.action.kick"), TagResolver.resolver(
                                Placeholder.parsed("reason", violations.name)
                            )
                        )
                    )
                    flagging.remove(player.uniqueId)
                }
            }
        }
        if (actionList.contains("notify")) {
            try {
                magenta.serverFeedback.client.send(magenta.serverFeedback.addEmbed {
                    setAuthor(WebhookEmbed.EmbedAuthor("Chat Filter 1.0.0 - Varování", null, null))
                    setThumbnailUrl(avatar.format(trimUUID(player.uniqueId)))
                    setDescription("Se pokusil napsat něco co je zakázáno !")
                    addField(WebhookEmbed.EmbedField(true, "Detekován Hráč", player.name))
                    addField(WebhookEmbed.EmbedField(true, "Modul", violations.name))
                    addField(WebhookEmbed.EmbedField(false, "Napsal", messageFromChat))
                    setFooter(WebhookEmbed.EmbedFooter("Detekováno ${now()}", null))
                })
            } catch (_: IllegalArgumentException) { }
            Bukkit.broadcast(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.filter.admin.notify"), TagResolver.resolver(
                Placeholder.parsed("player", player.name),
                Placeholder.parsed("flagged", violations.name),
                Placeholder.parsed("msg", messageFromChat))),
                "magenta.filter.notify"
            )
        }
        if (actionList.contains("message")) {
            if (translation != null) {
                player.sendMessage(ModernText.miniModernText(translation, Placeholder.parsed("player", player.name)))
            }
        }
        if (actionList.contains("cancel")) {
            event.isCancelled = true
        }
    }
}