package com.github.encryptsl.magenta.common.filter.modules

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.chat.Chat
import com.github.encryptsl.magenta.api.chat.enums.Violations
import com.github.encryptsl.magenta.common.filter.ChatPunishManager
import com.github.encryptsl.magenta.common.utils.CensorAPI
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import java.util.*


class AntiSpam(val magenta: Magenta, private val violations: Violations) : Chat {

    private val spam: MutableMap<UUID, String> = HashMap()

    private val chatPunishManager = ChatPunishManager(magenta, violations)

    override fun isDetected(event: AsyncChatEvent) {
        val player = event.player
        val uuid = player.uniqueId
        val message = PlainTextComponentSerializer.plainText().serialize(event.message())

        if (!magenta.config.getBoolean("chat.filters.${violations.name.lowercase()}.control")) return

        if (player.hasPermission("magenta.chat.filter.bypass.antispam"))
            return

        if (!spam.containsKey(uuid)) {
            spam[uuid] = message
            return
        }

        if (!spam[uuid].equals(message, true)) {
            spam[uuid] = message
            return
        }

        if (CensorAPI.checkSimilarity(message, spam[uuid].toString(), magenta.config.getInt("chat.filters.antispam.similarity"))) {
            magenta.schedulerMagenta.delayedTask(magenta, {
                      spam.remove(uuid, spam[uuid].toString())
            }, 1000L)
            chatPunishManager.action(player, event, magenta.localeConfig.getMessage("magenta.filter.antispam"),
                "Spamovat"
            )
        }
    }


}