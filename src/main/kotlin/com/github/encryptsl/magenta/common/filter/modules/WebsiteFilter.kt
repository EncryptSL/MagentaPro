package com.github.encryptsl.magenta.common.filter.modules

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.chat.Chat
import com.github.encryptsl.magenta.api.chat.enums.Violations
import com.github.encryptsl.magenta.common.filter.ChatPunishManager
import com.github.encryptsl.magenta.common.utils.ModernText
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer

class WebsiteFilter(private val magenta: Magenta, private val violations: Violations) : Chat {
    override fun isDetected(event: AsyncChatEvent) {
        val player = event.player
        val message = PlainTextComponentSerializer.plainText().serialize(event.message())
        val chatPunishManager = ChatPunishManager(magenta, violations)
        var detected = false

        if (!magenta.chatControl.getConfig().getBoolean("chat.filters.${violations.name.lowercase()}.control")) return

        if (player.hasPermission("magenta.chat.filter.bypass.websites"))
            return

        if(magenta.chatControl.getConfig().getStringList("chat.filter.${violations.name.lowercase()}.whitelist").contains(message))
            return

        for (m in message.split(" ")) {
            magenta.chatControl.getConfig().getStringList("chat.filters.${violations.name.lowercase()}.web_regex").forEach { regex ->
                if (m.contains(Regex("(.*)${regex}(.*)"))) {
                    detected = true
                }
            }
        }

        if (detected) {
            chatPunishManager.action(
                player,
                event,
                magenta.localeConfig.getMessage("magenta.filter.web_filter"),
                message
            )
        }
    }

    private fun hoverLink(link: String): Component {
        return ModernText.miniModernText("<green>[ODKAZ]").hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, ModernText.miniModernText("<red>Odkaz: na vlastní nebezpečí !"))).clickEvent(
            ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, link))
    }
}