package com.github.encryptsl.magenta.common.filter.modules

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.chat.Chat
import com.github.encryptsl.magenta.api.chat.enums.Violations
import com.github.encryptsl.magenta.common.extensions.compactCensoring
import com.github.encryptsl.magenta.common.filter.ChatPunishManager
import com.github.encryptsl.magenta.common.utils.ModernText
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.event.EventHandler
import java.util.regex.Pattern

class WebsiteFilter(private val magenta: Magenta, private val violations: Violations) : Chat {

    @EventHandler
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
                        ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.filter.web_filter")),TextReplacementConfig.builder()
                            .match(Pattern.compile(regex))
                            .replacement('*'.compactCensoring(5))
                            .build(), message)
                    return
                }
            }
        }
    }
}