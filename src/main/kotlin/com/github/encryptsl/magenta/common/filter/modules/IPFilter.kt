package com.github.encryptsl.magenta.common.filter.modules

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.chat.Chat
import com.github.encryptsl.magenta.api.chat.enums.Violations
import com.github.encryptsl.magenta.common.filter.ChatPunishManager
import com.github.encryptsl.magenta.common.utils.ModernText
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import java.util.regex.Pattern

class IPFilter(private val magenta: Magenta, private val violations: Violations) : Chat {
    override fun isDetected(event: AsyncChatEvent) {
        val player = event.player
        val chatPunishManager = ChatPunishManager(magenta, violations)
        val message = PlainTextComponentSerializer.plainText().serialize(event.message())

        if (!magenta.config.getBoolean("chat.filters.${violations.name.lowercase()}.control")) return

        if (player.hasPermission("magenta.chat.filter.bypass.ipfilter")) return

        for (m in message.split(" ") ) {
            if (m.matches(Regex("${magenta.config.getString("chat.filters.${violations.name.lowercase()}.ip_regex")}"))) {
                chatPunishManager.action(
                    player, event,
                    ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.filter.ip_filter")),
                    TextReplacementConfig
                        .builder()
                        .match(Pattern.compile(magenta.config.getString("chat.filters.${violations.name.lowercase()}.ip_regex").toString()))
                        .replacement(ModernText.miniModernText("<red>[IP-Adressa]"))
                        .build(),
                    message
                )
            }
        }
    }
}