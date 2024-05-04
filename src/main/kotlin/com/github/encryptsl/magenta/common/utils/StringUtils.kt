@file:Suppress("DEPRECATION")
package com.github.encryptsl.magenta.common.utils

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.extensions.expressionCalculation
import org.bukkit.ChatColor
import org.bukkit.OfflinePlayer
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

class StringUtils(private val magenta: Magenta) {
    fun inInList(key: String, value: String): Boolean
        = magenta.config.getStringList(key).contains(value)

    fun magentaPlaceholders(message: String, player: Player): String {
        return message.replace("{player}", player.name).replace("%player%", player.name)
    }

    fun arithmeticExpression(player: OfflinePlayer, config: FileConfiguration, path: String, value: Int = 0): String {
        val expressFormula = config.getString(path).toString()
            .replace("{level}", magenta.levelModel.getLevel(player.uniqueId).level.toString())
            .replace("{exp}", magenta.levelModel.getLevel(player.uniqueId).experience.toString())
            .replace("{votes}", magenta.vote.getPlayerVote(player.uniqueId).toString())
            .replace("{money}", magenta.vaultHook.getBalance(player).toString())
            .replace("{value}", value.toString())

        return expressionCalculation(expressFormula)
    }

    fun censorIp(ipAddress: String): String {
        val oktety = ipAddress.split(".")
        if (oktety.size != 4) {
            throw IllegalArgumentException("Not valid IP Address")
        }
        return "${oktety[0]}.${oktety[1]}.${oktety[2]}.xxx"
    }


    fun colorize(value: String): String = ChatColor.translateAlternateColorCodes('&', value)
}