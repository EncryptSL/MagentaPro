@file:Suppress("DEPRECATION")
package com.github.encryptsl.magenta.common.utils

import com.github.encryptsl.magenta.Magenta
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import kotlin.math.max
import kotlin.math.min

class StringUtils(private val magenta: Magenta) {
    fun inInList(key: String, value: String): Boolean
        = magenta.config.getStringList(key).contains(value)

    fun magentaPlaceholders(message: String, player: Player): String {
        return message.replace("{player}", player.name).replace("%player%", player.name)
    }

    fun censorIp(ipAddress: String): String {
        val oktety = ipAddress.split(".")
        if (oktety.size != 4) {
            throw IllegalArgumentException("Not valid IP Address")
        }
        return "${oktety[0]}.${oktety[1]}.${oktety[2]}.xxx"
    }

    private fun levenshteinDistance(str1: String, str2: String) : Int {
        val matrix = Array(str1.length + 1) {
            Array(str2.length + 1) { 0 }
        }

        for (i in 0..str1.length) {
            matrix[i][0] = i
        }

        for (i in 0..str2.length) {
            matrix[0][i] = i
        }

        for (i in 1..str1.length) {
            for (j in 1..str2.length) {
                if (str1[i - 1] == str2[j - 1]) {
                    matrix[i][j] = matrix[i - 1][j - 1]
                } else {
                    matrix[i][j] = min(min(matrix[i - 1][j], matrix[i][j - 1]), matrix[i - 1][j - 1]) + 1
                }
            }
        }

        return matrix[str1.length][str2.length]
    }

    private fun similarityScore(str1: String, str2: String): Int {
        val levenshteinDistance = levenshteinDistance(str1, str2)
        val longerStringLength = max(str1.length, str2.length)

        val result: Double = 1 - levenshteinDistance / longerStringLength.toDouble()
        return (result * 100).toInt()
    }

    fun checkSimilarity(str1: String, str2: String, maxSimiliarity: Int): Boolean {
        return (similarityScore(str1, str2) * 100) > maxSimiliarity
    }


    fun colorize(value: String): String = ChatColor.translateAlternateColorCodes('&', value)
}