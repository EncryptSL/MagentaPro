package com.github.encryptsl.magenta.common.filter.modules

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.chat.AbstractChatFilter
import com.github.encryptsl.magenta.api.chat.Chat
import com.github.encryptsl.magenta.api.chat.enums.Violations
import com.github.encryptsl.magenta.common.Algorithms
import com.github.encryptsl.magenta.common.filter.ChatPunishManager
import com.github.encryptsl.magenta.common.utils.ModernText
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.*
import java.util.concurrent.TimeUnit


class AntiSpam(val magenta: Magenta, private val violations: Violations) : Chat {

    private val spam: MutableMap<UUID, String> = HashMap()

    private val algorithms = Algorithms()
    val chatPunishManager = ChatPunishManager(magenta, violations)

    override fun isDetected(event: AsyncChatEvent) {
        return
    }


}