package com.github.encryptsl.magenta.api.clan

import com.github.encryptsl.magenta.common.database.entity.ClanEntity
import org.bukkit.entity.Player

interface ClanAPI {
    fun createClan(player: Player, clanName: String)
    fun clanExist(clanName: String): Boolean
    fun deleteClan(clanName: String)
    fun renameClan(clanName: String)
    fun addClanMember(clanName: String, username: String)
    fun setClanRole(clanName: String, username: String, clanRole: ClanRoles)
    fun getClan(clanName: String): ClanEntity
    fun topClans(): MutableMap<String, Int>
}