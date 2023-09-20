package com.github.encryptsl.magenta.common.database.models

import com.github.encryptsl.magenta.api.database.HomeSQL
import com.github.encryptsl.magenta.common.database.entity.HomeEntity
import com.github.encryptsl.magenta.common.database.tables.HomeTable
import org.bukkit.Location
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class HomeModel : HomeSQL {
    override fun createHome(player: Player, location: Location, home: String) {
        transaction {
            HomeTable.insert {
                it[username] = player.name
                it[uuid] = player.uniqueId.toString()
                it[HomeTable.home] = home
                it[world] = location.world.name
                it[x] = location.x.toInt()
                it[y] = location.y.toInt()
                it[z] = location.z.toInt()
                it[yaw] = location.yaw
                it[pitch] = location.pitch
            }
        }
    }

    override fun deleteHome(home: String) {
        transaction { HomeTable.deleteWhere { HomeTable.home eq home } }
    }

    override fun moveHome(home: String, location: Location) {
        transaction { HomeTable.update( { HomeTable.home eq home }) {
            it[world] = location.world.name
            it[x] = location.x.toInt()
            it[y] = location.y.toInt()
            it[z] = location.z.toInt()
            it[yaw] = location.yaw
            it[pitch] = location.pitch
        } }
    }

    override fun renameHome(oldHomeName: String, newHomeName: String) {
        transaction {
            HomeTable.update({ HomeTable.home eq oldHomeName }) {
                it[home] = newHomeName
            }
        }
    }

    override fun getHomeExist(home: String): Boolean {
        return transaction { !HomeTable.select(HomeTable.home eq home).empty() }
    }

    override fun <T> getHome(home: String, columnName: Expression<T>): T {
        return transaction {
            HomeTable.select(HomeTable.home eq home).first()[columnName]
        }
    }

    override fun getHomes(): List<HomeEntity> {
        return transaction { HomeTable.selectAll().map { r ->
            HomeEntity(
            r[HomeTable.username],
            r[HomeTable.uuid],
            r[HomeTable.home],
            r[HomeTable.world],
            r[HomeTable.x],
            r[HomeTable.y],
            r[HomeTable.z],
            r[HomeTable.pitch],
            r[HomeTable.yaw])
        } }
    }
}