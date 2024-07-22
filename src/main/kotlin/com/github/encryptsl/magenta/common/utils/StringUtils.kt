package com.github.encryptsl.magenta.common.utils

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.extensions.evaluate
import org.bukkit.OfflinePlayer
import org.bukkit.configuration.file.FileConfiguration

class StringUtils(private val magenta: Magenta) {

    fun inInList(key: String, value: String): Boolean
        = magenta.config.getStringList(key).contains(value)

    fun arithmeticExpression(player: OfflinePlayer, config: FileConfiguration, path: String, value: Int = 0): String {
        val getLevel = magenta.levelAPI.getUserByUUID(player.uniqueId).join()

        val expressFormula = config.getString(path).toString()
            .replace("{level}", getLevel.level.toString())
            .replace("{exp}", getLevel.experience.toString())
            .replace("{votes}", magenta.vote.getUserVotesByUUID(player.uniqueId).join().toString())
            .replace("{money}", magenta.vaultUnlockedHook.getBalance(player).toString())
            .replace("{value}", value.toString())

        return evaluate(expressFormula).toInt().toString()
    }
}