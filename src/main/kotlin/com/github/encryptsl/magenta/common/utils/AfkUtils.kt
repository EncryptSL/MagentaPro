package com.github.encryptsl.magenta.common.utils

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.extensions.datetime
import com.github.encryptsl.magenta.common.extensions.expire
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.entity.Player
import java.util.*

class AfkUtils(private val magenta: Magenta) {

    private val cachedTime: MutableMap<UUID, String> = HashMap()

    fun forceTime(uuid: UUID, time: Long) {
        cachedTime[uuid] = expire(time)
    }

    fun addTime(player: Player, time: Long) {
        if (Teams.haveTeam(player, "AFK")) {
            cachedTime[player.uniqueId] = expire(time)
            Teams.removeTeam(player, "AFK")
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.player.no.longer.afk"),
                Placeholder.parsed("player", player.name)
            ))
        }
    }

    private fun getTime(uuid: UUID): String
            = cachedTime[uuid].toString()

    fun clearCache(player: Player) {
        magenta.logger.info("AFK Cache for player ${player.name} was cleared !!!")
    }

    fun clearCache()
    {
        cachedTime.clear()
    }

    fun isAfk(uuid: UUID): Boolean
            = getTime(uuid) == datetime()

}