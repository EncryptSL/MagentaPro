package com.github.encryptsl.magenta.common.hook.placeholderapi

import com.github.encryptsl.kmono.lib.extensions.levelProgress
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.PluginPlaceholders
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer

class MagentaPlaceholderAPI(private val magenta: Magenta, private val version: String) : PlaceholderExpansion() {

    private val pluginPlaceholders: PluginPlaceholders by lazy { PluginPlaceholders(magenta) }

    override fun getIdentifier(): String = "magenta"
    override fun getAuthor(): String = "EncryptSL"
    override fun getVersion(): String = version
    override fun persist(): Boolean = true
    override fun getRequiredPlugin(): String = "MagentaPro"

    override fun canRegister(): Boolean {
        return magenta.pluginManager.getPlugin(requiredPlugin)?.isEnabled == true
    }

    override fun onRequest(player: OfflinePlayer, identifier: String): String? {
        val args = identifier.split("_")
        val string = args.getOrNull(1)?.toString()
        val rank = args.getOrNull(3)?.toIntOrNull()
        val user = magenta.user.getUser(player.uniqueId)
        val levelEntity = magenta.virtualLevel.getUserByUUID(player.uniqueId).join()


        if (identifier.startsWith("votes_")) {
            return user.getVotesByService(string.toString()).toString()
        }

        return when (identifier) {
            "afk" -> magenta.afk.isAfk(player.uniqueId).toString()
            "level" -> levelEntity.level.toString()
            "level_progress" -> levelProgress(levelEntity.level, levelEntity.experience).toString()
            "socialspy" -> user.isSocialSpy().toString()
            "vanished" -> user.isVanished().toString()
            "votes" -> user.getVotes().toString()
            "total_votes" -> magenta.vote.totalVotes().join().toString()
            "voteparty_now" -> magenta.voteParty.getVoteParty().join().currentVotes.toString()
            "voteparty_max" -> magenta.config.getInt("votifier.voteparty.start_at").toString()
            "voteparty_winner" -> magenta.voteParty.getVoteParty().join().lastWinnerOfParty ?: "NEVER"
            "top_vote_rank_player" -> pluginPlaceholders.topVoteNameByRank(1)
            else -> {
                rank?.let {
                    when {
                        identifier.startsWith("top_vote_name_") -> pluginPlaceholders.topVoteNameByRank(rank)
                        identifier.startsWith("top_vote_votes_") -> pluginPlaceholders.voteByRank(rank).toString()
                        identifier.startsWith("top_level_name_") -> pluginPlaceholders.topLevelNameByRank(rank)
                        identifier.startsWith("top_level_levels_") -> pluginPlaceholders.levelByRank(rank).toString()
                        else -> null
                    }
                }
            }
        }
    }
}