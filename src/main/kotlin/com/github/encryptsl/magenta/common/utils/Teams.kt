package com.github.encryptsl.magenta.common.utils

import com.github.encryptsl.magenta.Magenta
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Team

object Teams {
    fun createTeam(teamName: String, prefix: String) {
        val scoreboard: Scoreboard = Bukkit.getScoreboardManager().mainScoreboard

        if (scoreboard.getTeam(teamName) == null) {
            val team: Team = scoreboard.registerNewTeam(teamName)
            team.setCanSeeFriendlyInvisibles(true)
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER)
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS)
            team.prefix(Component.text(prefix))
        } else {
            Bukkit.getLogger().info("Team $teamName Exist")
        }
    }

    fun addTeam(player: Player, teamName: String) {
        val team = getTeam(teamName) ?: return
        if (!haveTeam(player, teamName)) {
            team.addPlayer(player)
        }
    }
    fun removeTeam(player: Player, teamName: String) {
        val team = getTeam(teamName) ?: return

        if (haveTeam(player, teamName)) {
            team.removePlayer(player)
        }
    }

    fun clear(teamName: String) {
        val players = Bukkit.getServer().offlinePlayers
        val board = Bukkit.getScoreboardManager().mainScoreboard
        val team = board.getTeam(teamName) ?: return
        players.filter { p -> team.hasPlayer(p) }.forEach { p -> team.removePlayer(p) }
    }

    private fun getTeam(teamName: String): Team? {
        val board = Bukkit.getScoreboardManager().mainScoreboard

        if (board.getTeam(teamName) != null)
            return board.getTeam(teamName)

        return null
    }

    private fun haveTeam(player: Player, teamName: String): Boolean {
        val team = getTeam(teamName) ?: return false

        return team.hasPlayer(player)
    }
}