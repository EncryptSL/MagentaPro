package com.github.encryptsl.magenta.common

class Algorithms {
    fun levenshteinDistance(s1: String, s2: String): Int {
        // Create a table to store the Levenshtein distances between prefixes of the two strings.
        val table = Array(s1.length + 1) { IntArray(s2.length + 1) }

        // Initialize the table.
        for (i in 0 until table.size) {
            table[i][0] = i
        }
        for (j in 0 until table[0].size) {
            table[0][j] = j
        }

        // Fill in the table.
        for (i in 1 until table.size) {
            for (j in 1 until table[0].size) {
                if (s1[i - 1] == s2[j - 1]) {
                    table[i][j] = table[i - 1][j - 1]
                } else {
                    table[i][j] = minOf(
                        table[i - 1][j] + 1, // Insert
                        table[i][j - 1] + 1, // Delete
                        table[i - 1][j - 1] + 1 // Replace
                    )
                }
            }
        }

        // Return the Levenshtein distance.
        return table[s1.length][s2.length]
    }

    fun checkSimilarity(message: String, lastMessage: String): Int {
        var longer = message
        var shorter = lastMessage
        if (message.length < lastMessage.length) {
            longer = lastMessage
            shorter = message
        }
        val longerLength = longer.length
        if (longerLength == 0) return 100
        val result: Double = (longerLength - levenshteinDistance(longer, shorter)) / longerLength.toDouble()
        return (result * 100).toInt()
    }
}