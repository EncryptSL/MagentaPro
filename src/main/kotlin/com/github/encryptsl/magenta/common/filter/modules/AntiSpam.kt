package com.github.encryptsl.magenta.common.filter.modules

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.Permissions
import com.github.encryptsl.magenta.common.filter.ChatCheck
import com.github.encryptsl.magenta.common.filter.impl.ChatFilters
import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import java.time.Duration
import java.util.*


class AntiSpam(val magenta: Magenta) : ChatCheck() {

    val spam: Cache<UUID, String> = CacheBuilder.newBuilder().expireAfterWrite(Duration.ofSeconds(60)).build()

    @EventHandler(priority = EventPriority.MONITOR)
    override fun handle(event: AsyncChatEvent) {
        val player = event.player
        val phrase = PlainTextComponentSerializer.plainText().serialize(event.message())

        if (matches(player, phrase)) {
            chatPunishManager().action(player, event, magenta.locale.getMessage("magenta.filter.antispam"), "Spamovat", ChatFilters.ANTISPAM)
        }
    }

    override fun matches(player: Player, phrase: String): Boolean {
        if (!magenta.chatControl.getConfig().getBoolean("filters.antispam.control")) return false

        if (player.hasPermission(Permissions.CHAT_FILTER_BYPASS_SPAM))
            return false

        spam.put(player.uniqueId, phrase)
        spam.asMap().computeIfPresent(player.uniqueId) { _, _ -> phrase }

        return spam.getIfPresent(player.uniqueId)?.let {
            magenta.stringUtils.checkSimilarity(phrase,
                it, magenta.chatControl.getConfig().getInt("filters.antispam.similarity"))
        } == true
    }
}