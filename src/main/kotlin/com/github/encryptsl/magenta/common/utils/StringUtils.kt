package com.github.encryptsl.magenta.common.utils

import com.github.encryptsl.magenta.Magenta
import org.bukkit.entity.Player

class StringUtils(private val magenta: Magenta) {

    fun isNickBlacklisted(nickname: String) : Boolean
        = magenta.config.getStringList("nick-blacklist").contains(replaceNickName(nickname))

    fun isNickInAllowedLength(nickname: String): Boolean
        = replaceNickName(nickname).length <= magenta.config.getInt("max-nick-length")

    fun replaceNickName(nickname: String): String {
        return nickname.replace(Regex("<*[a-zA-Z0-9#:]*>"), "")
    }

    fun magentaPlaceholders(message: String, player: Player): String {
        return message.replace("{player}", player.name).replace("%player%", player.name)
    }
}