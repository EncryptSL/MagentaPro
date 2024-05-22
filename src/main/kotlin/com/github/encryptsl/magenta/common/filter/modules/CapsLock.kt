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


class CapsLock(private val magenta: Magenta) : ChatCheck() {

    @EventHandler(priority = EventPriority.NORMAL)
    override fun handle(event: AsyncChatEvent) {
        val player = event.player
        val phrase = PlainTextComponentSerializer.plainText().serialize(event.message())
        if (matches(player, phrase)) {
            chatPunishManager().action(player, event, magenta.locale.getMessage("magenta.filter.caps"), "Psát CapsLockem", ChatFilters.CAPSLOCK)
        }
    }

    override fun matches(player: Player, phrase: String): Boolean {
        if (!magenta.chatControl.getConfig().getBoolean("filters.capslock.control")) return false

        if (player.hasPermission(Permissions.CHAT_FILTER_BYPASS_CAPS)) return false

        return (countUpperCaseLetters(phrase) > magenta.chatControl.getConfig().getInt("filters.capslock.sensitive", 15))
    }
}