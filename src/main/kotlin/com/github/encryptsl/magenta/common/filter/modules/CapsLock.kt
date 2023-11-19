package com.github.encryptsl.magenta.common.filter.modules

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.chat.Chat
import org.bukkit.entity.Player


class CapsLock(private val magenta: Magenta) : Chat {
    override fun isDetected(player: Player, phrase: String): Boolean {

        if (!magenta.chatControl.getConfig().getBoolean("chat.filters.capslock.control")) return false

        if (player.hasPermission("magenta.chat.filter.bypass.capslock")) return false

        val count = phrase.toCharArray().count { Character.isUpperCase(it) }

        return (count > magenta.chatControl.getConfig().getInt("chat.filters.capslock.sensitive", 15))
    }
}