@file:Suppress("DEPRECATION")
package com.github.encryptsl.magenta.common.utils

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.extensions.expressionCalculation
import org.bukkit.OfflinePlayer
import org.bukkit.configuration.file.FileConfiguration

class StringUtils(private val magenta: Magenta) {

    fun inInList(key: String, value: String): Boolean
        = magenta.config.getStringList(key).contains(value)

    fun arithmeticExpression(player: OfflinePlayer, config: FileConfiguration, path: String, value: Int = 0): String {
        val expressFormula = config.getString(path).toString()
            .replace("{level}", magenta.levelModel.getLevel(player.uniqueId).level.toString())
            .replace("{exp}", magenta.levelModel.getLevel(player.uniqueId).experience.toString())
            .replace("{votes}", magenta.vote.getPlayerVote(player.uniqueId).toString())
            .replace("{money}", magenta.vaultHook.getBalance(player).toString())
            .replace("{value}", value.toString())

        return expressionCalculation(expressFormula)
    }
}