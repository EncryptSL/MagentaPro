package com.github.encryptsl.magenta.api.chat

import com.github.encryptsl.magenta.common.filter.FilterManager
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.entity.Player

interface Chat {
    fun detection(event: AsyncChatEvent)
    fun filterManager(): FilterManager
}