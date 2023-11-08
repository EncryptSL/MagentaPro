package com.github.encryptsl.magenta.common.filter.modules

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.chat.Chat
import com.github.encryptsl.magenta.api.chat.enums.Violations
import com.github.encryptsl.magenta.common.filter.ChatPunishManager
import com.github.encryptsl.magenta.common.utils.ModernText
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import java.util.regex.Pattern

class WebsiteFilter(private val magenta: Magenta, private val violations: Violations) : Chat {
    override fun isDetected(event: AsyncChatEvent) {
        val player = event.player
        val message = PlainTextComponentSerializer.plainText().serialize(event.message())
        val chatPunishManager = ChatPunishManager(magenta, violations)

        if (!magenta.config.getBoolean("chat.filters.${violations.name.lowercase()}.control")) return

        if (player.hasPermission("magenta.chat.filter.bypass.websites"))
            return

        if(magenta.config.getStringList("chat.filter.${violations.name.lowercase()}.whitelist").contains(message))
            return

        for (m in message.split(" ")) {
            magenta.config.getStringList("chat.filters.${violations.name.lowercase()}.web_regex").forEach { regex ->
                if (m.contains(Regex("(.*)${regex}(.*)"))) {
                    chatPunishManager.action(player, event,
                        ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.filter.web_filter"), Placeholder.parsed("player", player.name)),TextReplacementConfig.builder()
                            .match(Pattern.compile(regex))
                            .replacement(hoverLink(m))
                            .build(), message)
                    return
                }
            }
        }
    }

    private fun hoverLink(link: String): Component {
        return ModernText.miniModernText("<green>[ODKAZ]").hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, ModernText.miniModernText("<red>Odkaz: na vlastní nebezpečí !"))).clickEvent(
            ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, link))
    }
}