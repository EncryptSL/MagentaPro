package com.github.encryptsl.magenta.common.chat.control.impl

import com.github.encryptsl.magenta.common.chat.control.ChatPunishManager
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.entity.Player

interface ChatDetection {
    fun handle(event: AsyncChatEvent)
    fun matches(player: Player, phrase: String): Boolean
    fun chatPunishManager(): ChatPunishManager
}