package com.github.encryptsl.magenta.common.filter.modules

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.chat.Chat
import com.github.encryptsl.magenta.api.chat.enums.Violations
import com.github.encryptsl.magenta.common.filter.ChatPunishManager
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import java.io.File
import java.util.*


class Swear(private val magenta: Magenta, private val violations: Violations) : Chat {
    override fun isDetected(event: AsyncChatEvent) {
        val player = event.player
        val message = PlainTextComponentSerializer.plainText().serialize(event.message())
        val chatPunishManager = ChatPunishManager(magenta, violations)
        var detected = false

        if (!magenta.config.getBoolean("chat.filters.${violations.name.lowercase()}.control")) return

        if (player.hasPermission("magenta.chat.filter.bypass.swear")) return

        val sc = Scanner(File(magenta.dataFolder, "swear_list.txt"))

        while (sc.hasNext()) {
            val s = sc.next()
            if (message.matches(Regex("(.*)$s(.*)"))) {
                detected = true
            }
        }
        if (detected) {
            chatPunishManager.action(
                player,
                event,
                magenta.localeConfig.getMessage("magenta.filter.swear"),
                message
            )
        }
    }


}