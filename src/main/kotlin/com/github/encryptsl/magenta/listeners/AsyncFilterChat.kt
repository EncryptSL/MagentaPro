package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.magenta.api.chat.Chat
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class AsyncFilterChat(private val chat: Chat) : Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onAsyncChat(event: AsyncChatEvent) {
        chat.isDetected(event)
    }

}