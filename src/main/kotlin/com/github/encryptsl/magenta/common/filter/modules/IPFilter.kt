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

class IPFilter(private val magenta: Magenta) : ChatCheck() {

    @EventHandler(priority = EventPriority.NORMAL)
    override fun handle(event: AsyncChatEvent) {
        val player = event.player
        val phrase = PlainTextComponentSerializer.plainText().serialize(event.message())

        if (matches(player, phrase)) {
            chatPunishManager().action(player, event, magenta.locale.getMessage("magenta.filter.ip_filter"), phrase, ChatFilters.IPFILTER)
        }
    }

    override fun matches(player: Player, phrase: String): Boolean {

        if (!magenta.chatControl.getConfig().getBoolean("filters.ipfilter.control")) return false

        if (player.hasPermission(Permissions.CHAT_FILTER_BYPASS_IP_ADDRESS)) return false

        return phrase.contains(Regex("${magenta.chatControl.getConfig().getString("filters.ipfilter.ip_regex")}"))
    }
}