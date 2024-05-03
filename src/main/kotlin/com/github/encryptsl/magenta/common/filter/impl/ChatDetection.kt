package com.github.encryptsl.magenta.common.filter.impl

import com.github.encryptsl.magenta.common.filter.ChatPunishManager
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.entity.Player

interface ChatDetection {
    fun handle(event: AsyncChatEvent)
    fun matches(player: Player, phrase: String): Boolean
    fun chatPunishManager(): ChatPunishManager
}