package com.github.encryptsl.magenta.common.filter.modules

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.chat.AbstractChatFilter
import com.github.encryptsl.magenta.api.chat.Violations
import io.papermc.paper.event.player.AsyncChatEvent
import jdk.internal.joptsimple.internal.Messages.message
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer


class CapsLock(private val magenta: Magenta, private val violations: Violations) : AbstractChatFilter(magenta, violations) {

    override fun detection(event: AsyncChatEvent) {
        val player= event.player
        val chatMessage = PlainTextComponentSerializer.plainText().serialize(event.message())
        val sensitive = magenta.config.getConfigurationSection("chat.filters.$magenta") ?: return

        if (!magenta.config.getBoolean("chat.filters.${violations.name}.control")) return

        var count = 0
        for (s in chatMessage.toCharArray()) {
            if (Character.isUpperCase(s)) {
                ++count
            }
        }

        if (count > sensitive.getInt("sensitive", 15)) {
            if (!sensitive.getBoolean("control")) {
                filterManager().action(player, event, magenta.locale.getProperty("magenta.filter.caps").toString().format(sensitive.getInt("sensitive"), count), null, null)
                event.isCancelled = true
            } else {
                event.renderer { _, _, message, _ -> message.replaceText(TextReplacementConfig.builder().match("[a-zA-Z]+").replacement(chatMessage.lowercase()).build()) }
            }
        }
    }
}