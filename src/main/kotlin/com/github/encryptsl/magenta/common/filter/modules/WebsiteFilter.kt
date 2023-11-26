package com.github.encryptsl.magenta.common.filter.modules

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.chat.Chat
import org.bukkit.entity.Player

class WebsiteFilter(private val magenta: Magenta) : Chat {
    override fun isDetected(player: Player, phrase: String): Boolean {
        var detected = false

        if (!magenta.chatControl.getConfig().getBoolean("filters.website.control")) return false

        if (player.hasPermission("magenta.chat.filter.bypass.websites"))
            return false

        if(magenta.chatControl.getConfig().getStringList("filters.website.whitelist").contains(phrase))
            return false

        for (m in phrase.split(" ")) {
            magenta.chatControl.getConfig().getStringList("filters.website.web_regex").forEach { regex ->
                if (m.contains(Regex("(.*)${regex}(.*)"))) {
                    detected = true
                }
            }
        }

        return detected
    }
}