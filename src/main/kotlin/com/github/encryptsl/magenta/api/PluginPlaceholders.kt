package com.github.encryptsl.magenta.api

import com.github.encryptsl.magenta.Magenta
import org.bukkit.entity.Player

class PluginPlaceholders(private val magenta: Magenta) {

    fun i8ln(player: Player, value: String): String {
        return value
            .replace("{username}", player.name)
            .replace("{user}", player.name)
            .replace("{player}", player.name)
            .replace("%player%", player.name)
            .replace("<player>", player.name)
    }

    fun topLevelNameByRank(rank: Int): String {
        val topLevel = topLevels()
        return if (rank in 1 .. topLevel.size) {
            topLevel.keys.elementAt(rank - 1)
        } else {
            "N/A"
        }
    }


    fun levelByRank(rank: Int): Int {
        val topLevel = topLevels()
        return if (rank in 1..topLevel.size) {
            topLevel.values.elementAt(rank - 1).toInt()
        } else {
            0
        }
    }

    fun topVoteNameByRank(rank: Int): String {
        val topVote = topVotes()
        return if (rank in 1..topVote.size) {
            topVote.keys.elementAt(rank - 1)
        } else {
            "N/A"
        }
    }

    fun voteByRank(rank: Int): Int {
        val topVote = topVotes()
        return if (rank in 1..topVote.size) {
            topVote.values.elementAt(rank - 1).toInt()
        } else {
            0
        }
    }

    fun topLevels(): Map<String, Int>
    {
        return magenta.virtualLevel.getLevels()
    }

    fun topVotes(): Map<String, Int> {
        return magenta.vote.votesLeaderBoard()
    }

}