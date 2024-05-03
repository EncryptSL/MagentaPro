package com.github.encryptsl.magenta.common.filter

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.filter.impl.ChatDetection
import org.bukkit.Bukkit
import org.bukkit.event.Listener

abstract class ChatCheck : ChatDetection, Listener {

    init {
        Bukkit.getPluginManager().registerEvents(this, Magenta.instance)
    }

    override fun chatPunishManager(): ChatPunishManager {
        return ChatPunishManager(Magenta.instance)
    }
}