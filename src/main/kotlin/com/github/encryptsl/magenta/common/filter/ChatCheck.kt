package com.github.encryptsl.magenta.common.filter

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.filter.impl.ChatDetection
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import kotlin.math.max
import kotlin.math.min

abstract class ChatCheck : ChatDetection, Listener {

    init {
        Bukkit.getPluginManager().registerEvents(this, Magenta.instance)
    }

    override fun chatPunishManager(): ChatPunishManager {
        return ChatPunishManager(Magenta.instance)
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

    fun countUpperCaseLetters(phrase: String): Int {
        return phrase.toCharArray().count { Character.isUpperCase(it) }
    }

    fun checkSimilarity(str1: String, str2: String, maxSimiliarity: Int): Boolean {
        return (similarityScore(str1, str2) >= maxSimiliarity)
    }
}