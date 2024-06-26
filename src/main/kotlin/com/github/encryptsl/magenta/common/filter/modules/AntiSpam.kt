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
import java.util.*


class AntiSpam(val magenta: Magenta) : ChatCheck() {

    @EventHandler(priority = EventPriority.NORMAL)
    override fun handle(event: AsyncChatEvent) {
        val player = event.player
        val phrase = PlainTextComponentSerializer.plainText().serialize(event.message())

        if (!isCachedPlayer(player.uniqueId)) {
            addMessage(player.uniqueId, phrase)
            return
        }

        if (!isCachedCurrentMessage(player.uniqueId, phrase)) {
            addMessage(player.uniqueId, phrase)
            return
        }

        val lastMessage = magenta.playerCacheManager.antiSpam.asMap().getValue(player.uniqueId)

        if (matches(player, lastMessage)) {
            chatPunishManager().action(player, event, magenta.locale.getMessage("magenta.filter.antispam"), "Spamovat", ChatFilters.ANTISPAM)
        }
    }

    private fun isCachedPlayer(uuid: UUID)
        =  magenta.playerCacheManager.antiSpam.asMap().containsKey(uuid)

    private fun isCachedCurrentMessage(uuid: UUID, message: String)
        =  magenta.playerCacheManager.antiSpam.asMap()[uuid].equals(message, true)

    private fun addMessage(uuid: UUID, message: String)
        =  magenta.playerCacheManager.antiSpam.asMap().put(uuid, message)

    override fun matches(player: Player, phrase: String): Boolean {
        if (!magenta.chatControl.getConfig().getBoolean("filters.antispam.control", false)) return false

        if (player.hasPermission(Permissions.CHAT_FILTER_BYPASS_SPAM))
            return false

        return checkSimilarity(phrase,  magenta.playerCacheManager.antiSpam.asMap()[player.uniqueId].toString(), magenta.chatControl.getConfig().getInt("filters.antispam.similarity"))
    }
}