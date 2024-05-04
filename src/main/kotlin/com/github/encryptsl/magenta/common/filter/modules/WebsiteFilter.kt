package com.github.encryptsl.magenta.common.filter.modules

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.Permissions
import com.github.encryptsl.magenta.common.filter.ChatCheck
import com.github.encryptsl.magenta.common.filter.impl.ChatFilters
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority

class WebsiteFilter(private val magenta: Magenta) : ChatCheck() {


    @EventHandler(priority = EventPriority.MONITOR)
    override fun handle(event: AsyncChatEvent) {
        val player = event.player
        val phrase = PlainTextComponentSerializer.plainText().serialize(event.message())

        if (matches(player, phrase)) {
            return chatPunishManager().action(player, event, magenta.locale.getMessage("magenta.filter.web_filter"), phrase, ChatFilters.WEBSITE)
        }
    }

    override fun matches(player: Player, phrase: String): Boolean {
        var detected = false

        if (!magenta.chatControl.getConfig().getBoolean("filters.website.control")) return false

        if (player.hasPermission(Permissions.CHAT_FILTER_BYPASS_WEBSITES))
            return false

        if(magenta.chatControl.getConfig().getStringList("filters.website.whitelist").contains(phrase))
            return false

        val webRegex = magenta.chatControl.getConfig().getStringList("filters.website.web_regex")

        for (m in phrase.split(" ")) {
            for (i in webRegex) {
                detected = m.contains(Regex("(.*)${i}(.*)"))
            }
        }
        return detected
    }
}