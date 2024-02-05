package com.github.encryptsl.magenta.common.filter.modules

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.CensorAPI
import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import org.bukkit.entity.Player
import java.time.Duration
import java.util.*


class AntiSpam(val magenta: Magenta) {

    fun isDetected(player: Player, phrase: String): Boolean {
        if (!magenta.chatControl.getConfig().getBoolean("filters.antispam.control")) return false

        if (player.hasPermission("magenta.chat.filter.bypass.antispam"))
            return false

        SpamCache.spam.put(player.uniqueId, phrase)
        SpamCache.spam.asMap().computeIfPresent(player.uniqueId) { _, _ -> phrase }

        return SpamCache.spam.getIfPresent(player.uniqueId)?.let {
            CensorAPI.checkSimilarity(phrase,
                it, magenta.chatControl.getConfig().getInt("filters.antispam.similarity"))
        } ?: false
    }
}

object SpamCache {
    val spam: Cache<UUID, String> = CacheBuilder.newBuilder().expireAfterWrite(Duration.ofSeconds(60)).build()
}