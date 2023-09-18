package com.github.encryptsl.magenta.api.chat

import com.github.encryptsl.magenta.common.filter.ChatPunishManager
import io.papermc.paper.event.player.AsyncChatEvent

interface Chat {
    fun detection(event: AsyncChatEvent)
    fun punishAction(): ChatPunishManager
}