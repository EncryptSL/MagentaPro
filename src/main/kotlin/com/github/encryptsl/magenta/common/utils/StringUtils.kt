@file:Suppress("DEPRECATION")
package com.github.encryptsl.magenta.common.utils

import com.github.encryptsl.magenta.Magenta
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class StringUtils(private val magenta: Magenta) {
    fun inInList(key: String, value: String): Boolean
        = magenta.config.getStringList(key).contains(value)

    fun replaceNickName(nickname: String): String {
        return nickname.replace(Regex("<*[a-zA-Z0-9#:]*>"), "")
    }

    fun magentaPlaceholders(message: String, player: Player): String {
        return message.replace("{player}", player.name).replace("%player%", player.name)
    }

    fun censorIp(ipAdresa: String): String {
        val oktety = ipAdresa.split(".")
        if (oktety.size != 4) {
            throw IllegalArgumentException("Not valid IP Address")
        }
        return "${oktety[0]}.${oktety[1]}.${oktety[2]}.XXX"
    }


    fun colorize(value: String): String = ChatColor.translateAlternateColorCodes('&', value)
}