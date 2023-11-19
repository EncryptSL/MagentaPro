package com.github.encryptsl.magenta.common.filter.modules

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.chat.Chat
import org.bukkit.entity.Player

class IPFilter(private val magenta: Magenta) : Chat {
    override fun isDetected(player: Player, phrase: String): Boolean {
        var detected = false

        if (!magenta.chatControl.getConfig().getBoolean("chat.filters.ipfilter.control")) return false

        if (player.hasPermission("magenta.chat.filter.bypass.ipfilter")) return false

        for (m in phrase.split(" ") ) {
            if (m.matches(Regex("${magenta.chatControl.getConfig().getString("chat.filters.ipfilter.ip_regex")}"))) {
                detected = true
            }
        }

        return detected
    }
}