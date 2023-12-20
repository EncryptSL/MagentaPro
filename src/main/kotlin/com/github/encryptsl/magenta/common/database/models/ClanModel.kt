package com.github.encryptsl.magenta.common.database.models

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.clan.ClanRoles
import com.github.encryptsl.magenta.common.database.entity.ClanEntity
import com.github.encryptsl.magenta.common.database.sql.ClanSQL
import com.github.encryptsl.magenta.common.database.tables.ClanTable
import com.google.gson.Gson
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class ClanModel(private val magenta: Magenta) : ClanSQL {
    override fun createClan(player: Player, clanName: String) {
        magenta.schedulerMagenta.doAsync(magenta) {
            ClanTable.insert {
                it[ClanTable.clanName] = clanName
                it[elo] = 1000
                it[members] = hashMapOf(player.name to ClanRoles.OWNER.name).toString()
            }
        }
    }

    override fun clanExist(clanName: String): Boolean
        = transaction { !ClanTable.select { ClanTable.clanName eq clanName }.empty() }

    override fun deleteClan(clanName: String) {
        magenta.schedulerMagenta.doAsync(magenta) {
            ClanTable.deleteWhere { ClanTable.clanName eq clanName }
        }
    }

    override fun renameClan(clanName: String) {
        magenta.schedulerMagenta.doAsync(magenta) {
            ClanTable.update({ ClanTable.clanName eq clanName }) {
                it[ClanTable.clanName] = clanName
            }
        }
    }

    override fun addClanMember(clanName: String, username: String) {
        magenta.schedulerMagenta.doAsync(magenta) {
            ClanTable.update({ClanTable.clanName eq clanName}) {
                it[members] = Gson().toJson(getClan(clanName).members.put(username, ClanRoles.MEMBER.name), HashMap::class.java)
            }
        }
    }

    override fun setClanRole(clanName: String, username: String, clanRole: ClanRoles) {
        magenta.schedulerMagenta.doAsync(magenta) {
            ClanTable.update({ClanTable.clanName eq clanName}) {
                it[members] = Gson().toJson(getClan(clanName).members.put(username, clanRole.name), HashMap::class.java)
            }
        }
    }

    override fun getClan(clanName: String): ClanEntity {
        val clan = transaction { ClanTable.select { ClanTable.clanName eq clanName }.first() }

        return ClanEntity(clan[ClanTable.clanName], Gson().fromJson(clan[ClanTable.members], HashMap<String, String>()::class.java), clan[ClanTable.elo])
    }

    override fun topClans(): MutableMap<String, Int> = transaction {
        ClanTable.selectAll().associate {
            it[ClanTable.clanName] to it[ClanTable.elo]
        }.toMutableMap()
    }
}