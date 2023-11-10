package com.github.encryptsl.magenta.common.filter.modules

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.chat.Chat
import com.github.encryptsl.magenta.api.chat.enums.Violations
import com.github.encryptsl.magenta.common.filter.ChatPunishManager
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer


class CapsLock(private val magenta: Magenta, private val violations: Violations) : Chat {

    private val chatPunishManager = ChatPunishManager(magenta, violations)

    override fun isDetected(event: AsyncChatEvent) {
        val player= event.player
        val chatMessage = PlainTextComponentSerializer.plainText().serialize(event.message())

        if (!magenta.config.getBoolean("chat.filters.${violations.name.lowercase()}.control")) return

        if (player.hasPermission("magenta.chat.filter.bypass.capslock")) return

        val count = chatMessage.toCharArray().count { Character.isUpperCase(it) }

        if (count > magenta.config.getInt("chat.filters.${violations.name.lowercase()}.sensitive", 15)) {
            chatPunishManager.action(
                player,
                event,
                magenta.localeConfig.getMessage("magenta.filter.caps"),
                "Psát CapsLockem"
            )
        }
    }
}