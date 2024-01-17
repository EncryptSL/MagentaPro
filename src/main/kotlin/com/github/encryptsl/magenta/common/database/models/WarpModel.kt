package com.github.encryptsl.magenta.common.database.models

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.database.entity.WarpEntity
import com.github.encryptsl.magenta.common.database.sql.WarpSQL
import com.github.encryptsl.magenta.common.database.tables.HomeTable
import com.github.encryptsl.magenta.common.database.tables.WarpTable
import org.bukkit.Location
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class WarpModel(private val magenta: Magenta) : WarpSQL {
    override fun creteWarp(player: Player, location: Location, warpName: String) {
        magenta.schedulerMagenta.doAsync(magenta) {
            transaction {
                WarpTable.insertIgnore {
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
    }

    override fun deleteWarp(warpName: String) {
        magenta.schedulerMagenta.doAsync(magenta) {
            transaction { WarpTable.deleteWhere { WarpTable.warpName eq warpName } }
        }
    }

    override fun deleteWarp(player: Player, warpName: String) {
        magenta.schedulerMagenta.doAsync(magenta) {
            transaction { WarpTable.deleteWhere { (uuid eq player.uniqueId.toString()) and (WarpTable.warpName eq warpName) } }
        }
    }

    override fun moveWarp(warpName: String, location: Location) {
        magenta.schedulerMagenta.doAsync(magenta) {
            transaction { WarpTable.update( { WarpTable.warpName eq warpName }) {
                it[world] = location.world.name
                it[x] = location.x.toInt()
                it[y] = location.y.toInt()
                it[z] = location.z.toInt()
                it[yaw] = location.yaw
                it[pitch] = location.pitch
            } }
        }
    }

    override fun moveWarp(player: Player, warpName: String, location: Location) {
        magenta.schedulerMagenta.doAsync(magenta) {
            transaction { WarpTable.update( { (WarpTable.uuid eq player.uniqueId.toString()) and (WarpTable.warpName eq warpName) }) {
                it[world] = location.world.name
                it[x] = location.x.toInt()
                it[y] = location.y.toInt()
                it[z] = location.z.toInt()
                it[yaw] = location.yaw
                it[pitch] = location.pitch
            } }
        }
    }

    override fun renameWarp(oldWarpName: String, newWarpName: String) {
        magenta.schedulerMagenta.doAsync(magenta) {
            transaction {
                WarpTable.update({ WarpTable.warpName eq oldWarpName }) {
                    it[warpName] = newWarpName
                }
            }
        }
    }

    override fun renameWarp(player: Player, oldWarpName: String, newWarpName: String) {
        magenta.schedulerMagenta.doAsync(magenta) {
            transaction {
                WarpTable.update({ (WarpTable.uuid eq player.uniqueId.toString()) and (WarpTable.warpName eq oldWarpName) }) {
                    it[warpName] = newWarpName
                }
            }
        }
    }

    override fun getWarpExist(warpName: String): Boolean {
        return transaction { !WarpTable.select(WarpTable.warpName).where(WarpTable.warpName eq warpName).empty() }
    }

    override fun canSetWarp(player: Player): Boolean {
        if (player.hasPermission("magenta.warps.unlimited")) return true

        val section = magenta.config.getConfigurationSection("warps.groups") ?: return false

        val max = section.getKeys(false).filter { player.hasPermission("magenta.warps.$it") }.map { section.getInt(it) }.first()

        if (max == -1) return true


        return transaction { HomeTable.select(HomeTable.uuid).where(HomeTable.uuid eq player.uniqueId.toString()).count() < max }
    }

    override fun <T> getWarp(warpName: String, columnName: Expression<T>): T {
        return transaction {
            WarpTable.select(WarpTable.warpName).where(WarpTable.warpName eq warpName).first()[columnName]
        }
    }

    override fun getWarps(): List<WarpEntity> {
        return transaction { WarpTable.selectAll().mapNotNull { r ->
            WarpEntity(
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