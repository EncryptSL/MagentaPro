package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.chat.Violations
import com.github.encryptsl.magenta.common.filter.modules.AdvancedFilter
import com.github.encryptsl.magenta.common.filter.modules.AntiSpam
import com.github.encryptsl.magenta.common.filter.modules.CapsLock
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class AsyncChatListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun chat(event: AsyncChatEvent) {
        AntiSpam(magenta, Violations.SPAM).detection(event)
        CapsLock(magenta, Violations.CAPSLOCK).detection(event)
        AdvancedFilter(magenta, Violations.ADVANCED_FILTER).detection(event)
    }
}