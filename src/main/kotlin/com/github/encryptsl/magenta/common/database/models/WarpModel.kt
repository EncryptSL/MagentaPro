package com.github.encryptsl.magenta.common.database.models

import com.github.encryptsl.magenta.api.database.WarpSQL
import com.github.encryptsl.magenta.common.database.entity.HomeEntity
import com.github.encryptsl.magenta.common.database.tables.HomeTable
import com.github.encryptsl.magenta.common.database.tables.WarpTable
import org.bukkit.Location
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class WarpModel : WarpSQL {
    override fun creteWarp(player: Player, location: Location, warpName: String) {
        transaction {
            WarpTable.insert {
                it[username] = player.name
                it[uuid] = player.uniqueId.toString()
                it[WarpTable.warpName] = warpName
                it[world] = location.world.name
                it[x] = location.x.toInt()
                it[y] = location.y.toInt()
                it[z] = location.z.toInt()
                it[yaw] = location.yaw
                it[pitch] = location.pitch
            }
        }
    }

    override fun deleteWarp(warpName: String) {
        transaction { WarpTable.deleteWhere { WarpTable.warpName eq warpName } }
    }

    override fun moveWarp(warpName: String, location: Location) {
        transaction { WarpTable.update( { WarpTable.warpName eq warpName }) {
            it[world] = location.world.name
            it[x] = location.x.toInt()
            it[y] = location.y.toInt()
            it[z] = location.z.toInt()
            it[yaw] = location.yaw
            it[pitch] = location.pitch
        } }
    }

    override fun renameWarp(oldWarpName: String, newWarpName: String) {
        transaction {
            WarpTable.update({ WarpTable.warpName eq oldWarpName }) {
                it[warpName] = newWarpName
            }
        }
    }

    override fun getWarpExist(warpName: String): Boolean {
        return transaction { !WarpTable.select(WarpTable.warpName eq warpName).empty() }
    }

    override fun <T> getWarp(warpName: String, columnName: Expression<T>): T {
        return transaction {
            WarpTable.select(WarpTable.warpName eq warpName).first()[columnName]
        }
    }

    override fun getWarps(): List<HomeEntity> {
        return transaction { HomeTable.selectAll().map { r ->
            HomeEntity(
                r[WarpTable.username],
                r[WarpTable.uuid],
                r[WarpTable.warpName],
                r[WarpTable.world],
                r[WarpTable.x],
                r[WarpTable.y],
                r[WarpTable.z],
                r[WarpTable.pitch],
                r[WarpTable.yaw])
        } }
    }
}