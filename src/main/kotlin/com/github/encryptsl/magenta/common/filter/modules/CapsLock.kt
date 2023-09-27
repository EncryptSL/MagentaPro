package com.github.encryptsl.magenta.common.filter.modules

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.chat.Chat
import com.github.encryptsl.magenta.api.chat.enums.Violations
import com.github.encryptsl.magenta.common.filter.ChatPunishManager
import com.github.encryptsl.magenta.common.utils.ModernText
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer


class CapsLock(private val magenta: Magenta, private val violations: Violations) : Chat {

    private val chatPunishManager = ChatPunishManager(magenta, violations)

    override fun isDetected(event: AsyncChatEvent) {
        val player= event.player
        val chatMessage = PlainTextComponentSerializer.plainText().serialize(event.message())
        val sensitive = magenta.config.getConfigurationSection("chat.filters.${violations.name.lowercase()}") ?: return

        //if (!sensitive.getBoolean("chat.filters.${violations.name.lowercase()}.control")) return

        //if (player.hasPermission("magenta.chat.filter.bypass.capslock")) return
        var count = 0
        for (s in chatMessage.toCharArray()) {
            if (Character.isUpperCase(s)) {
                ++count
            }
        }

        println(count)

        if (count > sensitive.getInt("chat.filters.${violations.name.lowercase()}.sensitive", 15)) {
            val a = chatMessage.lowercase()
            chatPunishManager.action(player, event, ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.filter.caps"), TagResolver.resolver(
                Placeholder.parsed("min", magenta.config.getString("chat.filters.capslock.sensitive").toString()),
                Placeholder.parsed("max", count.toString())
            )), TextReplacementConfig
                .builder()
                .match("[A-Z]*")
                .replacement(a)
                .build(),
                chatMessage
            )
        }
    }
}