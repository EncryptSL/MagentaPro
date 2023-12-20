package com.github.encryptsl.magenta.api.clan

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.database.entity.ClanEntity
import org.bukkit.entity.Player

class Clan(private val magenta: Magenta) : ClanAPI {
    override fun createClan(player: Player, clanName: String) {
        if (!clanExist(clanName)) return

        magenta.clanModel.createClan(player, clanName)
    }

    override fun clanExist(clanName: String): Boolean {
        return magenta.clanModel.clanExist(clanName)
    }

    override fun deleteClan(clanName: String) {
        magenta.clanModel.deleteClan(clanName)
    }

    override fun renameClan(clanName: String) {
        magenta.clanModel.renameClan(clanName)
    }

    override fun addClanMember(clanName: String, username: String) {
        magenta.clanModel.addClanMember(clanName, username)
    }

    override fun setClanRole(clanName: String, username: String, clanRole: ClanRoles) {
        magenta.clanModel.setClanRole(clanName, username, clanRole)
    }

    override fun getClan(clanName: String): ClanEntity {
        return magenta.clanModel.getClan(clanName)
    }

    override fun topClans(): MutableMap<String, Int> {
        return magenta.clanModel.topClans()
    }
}