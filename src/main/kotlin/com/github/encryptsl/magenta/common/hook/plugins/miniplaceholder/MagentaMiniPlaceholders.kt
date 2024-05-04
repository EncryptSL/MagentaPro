package com.github.encryptsl.magenta.common.hook.miniplaceholder

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.PluginPlaceholders
import com.github.encryptsl.magenta.api.level.LevelFormula
import com.github.encryptsl.magenta.common.hook.model.PluginHook
import io.github.miniplaceholders.kotlin.asInsertingTag
import io.github.miniplaceholders.kotlin.expansion
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

class MagentaMiniPlaceholders(private val magenta: Magenta) : PluginHook("MiniPlaceholders") {

    private val pluginPlaceholders: PluginPlaceholders by lazy { PluginPlaceholders(magenta) }

    fun register() {
        if (!isPluginEnabled()) return
        val expansion = expansion("magenta") {
            audiencePlaceholder("afk") { p, _, _ ->
                val player  = p as Player
                return@audiencePlaceholder Component.text(magenta.afk.isAfk(player.uniqueId)).asInsertingTag()
            }
            audiencePlaceholder("level") { p, _, _ ->
                val player  = p as Player
                return@audiencePlaceholder Component.text(magenta.virtualLevel.getLevel(player.uniqueId).level).asInsertingTag()
            }
            audiencePlaceholder("level_progress") { p, _, _ ->
                val player  = p as Player
                val levelEntity = magenta.virtualLevel.getLevel(player.uniqueId)
                return@audiencePlaceholder Component.text(LevelFormula.levelProgress(levelEntity.level, levelEntity.experience)).asInsertingTag()
            }
            audiencePlaceholder("socialspy") { p, _, _ ->
                val player  = p as Player
                return@audiencePlaceholder Component.text(magenta.user.getUser(player.uniqueId).isSocialSpy()).asInsertingTag()
            }
            audiencePlaceholder("vanished") { p, _, _ ->
                val player  = p as Player
                return@audiencePlaceholder Component.text(magenta.user.getUser(player.uniqueId).isVanished()).asInsertingTag()
            }
            audiencePlaceholder("votes") { p, _, _ ->
                val player  = p as Player
                return@audiencePlaceholder Component.text(magenta.user.getUser(player.uniqueId).getVotes()).asInsertingTag()
            }
            globalPlaceholder("votes") { _, _ ->
                return@globalPlaceholder Component.text(magenta.vote.totalVotes()).asInsertingTag()
            }
            globalPlaceholder("voteparty_now") { _, _ ->
                return@globalPlaceholder Component.text(magenta.voteParty.getVoteParty().currentVotes).asInsertingTag()
            }
            globalPlaceholder("voteparty_max") { _, _ ->
                return@globalPlaceholder Component.text(magenta.config.getInt("votifier.voteparty.start_at")).asInsertingTag()
            }
            globalPlaceholder("voteparty_last_winner") { _, _ ->
                return@globalPlaceholder Component.text(magenta.voteParty.getVoteParty().lastWinnerOfParty ?: "NEVER").asInsertingTag()
            }
            globalPlaceholder("top_vote") { _, _ ->
                return@globalPlaceholder Component.text(pluginPlaceholders.topVoteNameByRank(1)).asInsertingTag()
            }
            globalPlaceholder("votes_leaderboard_player") { i, _ ->
                return@globalPlaceholder Component.text(pluginPlaceholders.topVoteNameByRank(i.popOr("You need provide position.").value().toInt())).asInsertingTag()
            }
            globalPlaceholder("votes_leaderboard_votes_vote") { i, _ ->
                return@globalPlaceholder Component.text(pluginPlaceholders.voteByRank(i.popOr("You need provide position.").value().toInt())).asInsertingTag()
            }
            globalPlaceholder("levels_leaderboard_player") { i, _ ->
                return@globalPlaceholder Component.text(pluginPlaceholders.topLevelNameByRank(i.popOr("You need provide position.").value().toInt())).asInsertingTag()
            }
            globalPlaceholder("levels_leaderboard_level") { i, _ ->
                return@globalPlaceholder Component.text(pluginPlaceholders.levelByRank(i.popOr("You need provide position.").value().toInt())).asInsertingTag()
            }
        }
        expansion.register()
    }
}