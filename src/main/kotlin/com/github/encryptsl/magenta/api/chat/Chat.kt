package com.github.encryptsl.magenta.api.chat

import io.papermc.paper.event.player.AsyncChatEvent

interface Chat {
    fun isDetected(event: AsyncChatEvent)
}