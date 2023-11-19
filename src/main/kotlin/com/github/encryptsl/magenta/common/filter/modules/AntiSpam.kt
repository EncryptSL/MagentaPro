package com.github.encryptsl.magenta.common.filter.modules

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.CensorAPI
import org.bukkit.entity.Player
import java.util.*


class AntiSpam(val magenta: Magenta) {

    fun isDetected(player: Player, phrase: String): Boolean {
        if (!magenta.chatControl.getConfig().getBoolean("chat.filters.antispam.control")) return false

        if (player.hasPermission("magenta.chat.filter.bypass.antispam"))
            return false

        SpamCache.spam.putIfAbsent(player.uniqueId, phrase)
        SpamCache.spam.computeIfPresent(player.uniqueId) { _, _ -> phrase }

        return CensorAPI.checkSimilarity(phrase, SpamCache.spam[player.uniqueId].toString(), magenta.chatControl.getConfig().getInt("chat.filters.antispam.similarity"))
    }
}

object SpamCache {
    val spam: MutableMap<UUID, String> = HashMap()
}