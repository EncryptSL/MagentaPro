package com.github.encryptsl.magenta.common.filter.modules

import com.github.encryptsl.kmono.lib.utils.TextFilReader
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.Permissions
import com.github.encryptsl.magenta.common.filter.ChatCheck
import com.github.encryptsl.magenta.common.filter.impl.ChatFilters
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority


class Swear(private val magenta: Magenta) : ChatCheck() {

    @EventHandler(priority = EventPriority.NORMAL)
    override fun handle(event: AsyncChatEvent) {
        val player = event.player
        val phrase = PlainTextComponentSerializer.plainText().serialize(event.message())

        if (matches(player, phrase)) {
            return chatPunishManager().action(player, event, magenta.locale.getMessage("magenta.filter.swear"), phrase, ChatFilters.SWEAR)
        }
    }

    override fun matches(player: Player, phrase: String): Boolean {
        if (!magenta.chatControl.getConfig().getBoolean("filters.swear.control")) return false

        if (player.hasPermission(Permissions.CHAT_FILTER_BYPASS_SWEAR)) return false

        return TextFilReader.getReadableFile(magenta.dataFolder, "chatcontrol/swears.txt").find { phrase.matches(Regex("(.*)$it(.*)")) }.toBoolean()
    }


}