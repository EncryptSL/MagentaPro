package com.github.encryptsl.magenta.common.hook.placeholderapi

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.account.PlayerAccount
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.util.*
import kotlin.collections.LinkedHashMap

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
        val rank = args.getOrNull(2)?.toIntOrNull()
        val account = PlayerAccount(magenta, player.uniqueId)


        if (identifier.startsWith("votes_")) {
            return magenta.vote.getPlayerVote(player.uniqueId, string.toString())?.vote.toString()
        }

        return when (identifier) {
            "socialspy" -> account.getAccount().getBoolean("socialspy").toString()
            "vanish" -> account.getAccount().getBoolean("vanished").toString()
            "votes" -> magenta.vote.getPlayerVote(player.uniqueId).toString()
            "total_votes" -> magenta.vote.totalVotes().toString()
            "voteparty_now" -> magenta.vote.getVotesForParty().toString()
            "voteparty_max" -> magenta.config.getInt("votifier.voteparty.start_party").toString()
            "top_vote_rank_player" -> topVoteNameByRank(1)
            else -> {
                rank?.let {
                    when {
                        identifier.startsWith("top_vote_") -> topVoteNameByRank(rank)
                        identifier.startsWith("top_votes_") -> voteByRank(rank).toString()
                        else -> null
                    }
                }
            }
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

    private fun topVotes(): LinkedHashMap<String, Int> {
        return magenta.vote.topVotes()
            .filterKeys { it -> Bukkit.getOfflinePlayer(UUID.fromString(it)).hasPlayedBefore() }
            .toList()
            .sortedByDescending { (_, vote) -> vote }
            .toMap()
            .let { LinkedHashMap(it) }
    }
}