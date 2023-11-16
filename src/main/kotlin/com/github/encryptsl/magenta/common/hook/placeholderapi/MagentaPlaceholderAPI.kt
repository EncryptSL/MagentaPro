package com.github.encryptsl.magenta.common.hook.placeholderapi

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.account.UserAccount
import com.github.encryptsl.magenta.api.level.LevelFormula
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.util.*

class MagentaPlaceholderAPI(private val magenta: Magenta, private val version: String) : PlaceholderExpansion() {
    override fun getIdentifier(): String = "magenta"
    override fun getAuthor(): String = "EncryptSL"
    override fun getVersion(): String = version
    override fun persist(): Boolean = true
    override fun getRequiredPlugin(): String = "MagentaPro"

    override fun canRegister(): Boolean {
        return magenta.pluginManager.getPlugin(requiredPlugin)?.isEnabled ?: false
    }

    override fun onRequest(player: OfflinePlayer, identifier: String): String? {
        val args = identifier.split("_")
        val string = args.getOrNull(1)?.toString()
        val rank = args.getOrNull(3)?.toIntOrNull()
        val account = UserAccount(magenta, player.uniqueId)
        val levelEntity = magenta.virtualLevel.getLevel(player.uniqueId)


        if (identifier.startsWith("votes_")) {
            return magenta.vote.getPlayerVote(player.uniqueId, string.toString())?.vote.toString()
        }

        return when (identifier) {
            "afk" -> magenta.afk.isAfk(player.uniqueId).toString()
            "level" -> levelEntity.level.toString()
            "level_progress" -> LevelFormula.levelProgress(levelEntity.level, levelEntity.experience).toString()
            "socialspy" -> account.getAccount().getBoolean("socialspy").toString()
            "vanished" -> account.getAccount().getBoolean("vanished").toString()
            "votes" -> magenta.vote.getPlayerVote(player.uniqueId).toString()
            "total_votes" -> magenta.vote.totalVotes().toString()
            "voteparty_now" -> magenta.config.getInt("votifier.voteparty.current_votes").toString()
            "voteparty_max" -> magenta.config.getInt("votifier.voteparty.start_at").toString()
            "top_vote_rank_player" -> topVoteNameByRank(1)
            else -> {
                rank?.let {
                    when {
                        identifier.startsWith("top_vote_name_") -> topVoteNameByRank(rank)
                        identifier.startsWith("top_vote_votes_") -> voteByRank(rank).toString()
                        identifier.startsWith("top_level_name_") -> topLevelNameByRank(rank)
                        identifier.startsWith("top_level_levels_") -> levelByRank(rank).toString()
                        else -> null
                    }
                }
            }
        }
    }

    private fun topLevelNameByRank(rank: Int): String {
        val topLevel = topLevels()
        return if (rank in 1 .. topLevel.size) {
            val uuid = topLevel.keys.elementAt(rank - 1)
            Bukkit.getOfflinePlayer(UUID.fromString(uuid)).name ?: "UNKNOWN"
        } else {
            "EMPTY"
        }
    }


    private fun levelByRank(rank: Int): Int {
        val topLevel = topLevels()
        return if (rank in 1..topLevel.size) {
            topLevel.values.elementAt(rank - 1).toInt()
        } else {
            0
        }
    }

    private fun topVoteNameByRank(rank: Int): String {
        val topVote = topVotes()
        return if (rank in 1..topVote.size) {
            val uuid = topVote.keys.elementAt(rank - 1)
            Bukkit.getOfflinePlayer(UUID.fromString(uuid)).name ?: "UNKNOWN"
        } else {
            "EMPTY"
        }
    }

    private fun voteByRank(rank: Int): Int {
        val topVote = topVotes()
        return if (rank in 1..topVote.size) {
            topVote.values.elementAt(rank - 1).toInt()
        } else {
            0
        }
    }

    private fun topLevels(): LinkedHashMap<String, Int>
    {
        return magenta.virtualLevel.getLevels()
            .filterKeys { Bukkit.getOfflinePlayer(UUID.fromString(it)).hasPlayedBefore() }
            .toList()
            .sortedByDescending { (_, level) -> level }
            .toMap()
            .let { LinkedHashMap(it) }
    }

    private fun topVotes(): LinkedHashMap<String, Int> {
        return magenta.vote.topVotes()
            .filterKeys { Bukkit.getOfflinePlayer(UUID.fromString(it)).hasPlayedBefore() }
            .toList()
            .sortedByDescending { (_, vote) -> vote }
            .toMap()
            .let { LinkedHashMap(it) }
    }
}