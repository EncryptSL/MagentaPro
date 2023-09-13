package com.github.encryptsl.magenta.api.chat

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.filter.FilterManager
import io.papermc.paper.event.player.AsyncChatEvent

abstract class AbstractChatFilter(magenta: Magenta, violations: Violations) : Chat {

    private val filterManager = FilterManager(magenta, violations)

    override fun filterManager(): FilterManager {
        return filterManager
    }

}