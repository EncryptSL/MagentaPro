package com.github.encryptsl.magenta.common.filter.modules

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.chat.Chat
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import org.bukkit.entity.Player

class WebsiteFilter(private val magenta: Magenta) : Chat {
    override fun isDetected(player: Player, phrase: String): Boolean {
        var detected = false

        if (!magenta.chatControl.getConfig().getBoolean("chat.filters.website.control")) return false

        if (player.hasPermission("magenta.chat.filter.bypass.websites"))
            return false

        if(magenta.chatControl.getConfig().getStringList("chat.filter.website.whitelist").contains(phrase))
            return false

        for (m in phrase.split(" ")) {
            magenta.chatControl.getConfig().getStringList("chat.filters.website.web_regex").forEach { regex ->
                if (m.contains(Regex("(.*)${regex}(.*)"))) {
                    detected = true
                }
            }
        }

        return detected
    }

    private fun hoverLink(link: String): Component {
        return ModernText.miniModernText("<green>[ODKAZ]").hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, ModernText.miniModernText("<red>Odkaz: na vlastní nebezpečí !"))).clickEvent(
            ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, link))
    }
}