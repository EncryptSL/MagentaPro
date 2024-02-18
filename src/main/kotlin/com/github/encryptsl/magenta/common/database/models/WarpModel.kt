package com.github.encryptsl.magenta.common.database.models

import com.github.encryptsl.magenta.api.scheduler.SchedulerMagenta
import com.github.encryptsl.magenta.common.database.entity.WarpEntity
import com.github.encryptsl.magenta.common.database.sql.WarpSQL
import com.github.encryptsl.magenta.common.database.tables.HomeTable
import com.github.encryptsl.magenta.common.database.tables.WarpTable
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class WarpModel(private val plugin: Plugin) : WarpSQL {
    override fun creteWarp(player: Player, location: Location, warpName: String) {
        SchedulerMagenta.doAsync(plugin) {
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
        SchedulerMagenta.doAsync(plugin) {
            transaction { WarpTable.deleteWhere { WarpTable.warpName eq warpName } }
        }
    }

    override fun deleteWarp(player: Player, warpName: String) {
        SchedulerMagenta.doAsync(plugin) {
            transaction { WarpTable.deleteWhere { (uuid eq player.uniqueId.toString()) and (WarpTable.warpName eq warpName) } }
        }
    }

    override fun moveWarp(warpName: String, location: Location) {
        SchedulerMagenta.doAsync(plugin) {
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
        SchedulerMagenta.doAsync(plugin) {
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
        SchedulerMagenta.doAsync(plugin) {
            transaction {
                WarpTable.update({ WarpTable.warpName eq oldWarpName }) {
                    it[warpName] = newWarpName
                }
            }
        }
    }

    override fun renameWarp(player: Player, oldWarpName: String, newWarpName: String) {
        SchedulerMagenta.doAsync(plugin) {
            transaction {
                WarpTable.update({ (WarpTable.uuid eq player.uniqueId.toString()) and (WarpTable.warpName eq oldWarpName) }) {
                    it[warpName] = newWarpName
                }
            }
        }
    }

    override fun setWarpIcon(player: Player, warpName: String, icon: String) {
        SchedulerMagenta.doAsync(plugin) {
            transaction {
                WarpTable.update({(WarpTable.uuid eq player.uniqueId.toString()) and (WarpTable.warpName eq warpName)}) {
                    it[warpIcon] = icon
                }
            }
        }
    }


    override fun getWarpExist(warpName: String): Boolean {
        return transaction { !WarpTable.select(WarpTable.warpName).where(WarpTable.warpName eq warpName).empty() }
    }

    override fun canSetWarp(player: Player): Boolean {
        if (player.hasPermission("magenta.warps.unlimited")) return true

        val section = plugin.config.getConfigurationSection("warps.groups") ?: return false

        val max = section.getKeys(false).filter { player.hasPermission("magenta.warps.$it") }.map { section.getInt(it) }.first()

        if (max == -1) return true


        return transaction { HomeTable.select(HomeTable.uuid).where(HomeTable.uuid eq player.uniqueId).count() < max }
    }

    override fun getWarp(warpName: String): WarpEntity {
       val row = transaction {
            WarpTable.selectAll().where(WarpTable.warpName eq warpName).first()
        }

        return rowResultToWarpEntity(row)
    }

    override fun toLocation(warpName: String): Location {
        val rowResult = getWarp(warpName)

        return Location(Bukkit.getWorld(rowResult.world), rowResult.x.toDouble(),
            rowResult.y.toDouble(), rowResult.z.toDouble(), rowResult.yaw, rowResult.pitch)
    }

    override fun getWarpsByOwner(player: Player): List<WarpEntity> {
        return transaction { WarpTable.selectAll().where(WarpTable.uuid eq player.uniqueId.toString()).mapNotNull { rowResultToWarpEntity(it) } }
    }

    override fun getWarps(): List<WarpEntity> {
        return transaction { WarpTable.selectAll().mapNotNull {rowResultToWarpEntity(it)} }
    }

    private fun rowResultToWarpEntity(row: ResultRow): WarpEntity {
        return WarpEntity(
            row[WarpTable.username],
            row[WarpTable.uuid],
            row[WarpTable.warpName],
            row[WarpTable.warpIcon],
            row[WarpTable.world],
            row[WarpTable.x],
            row[WarpTable.y],
            row[WarpTable.z],
            row[WarpTable.pitch],
            row[WarpTable.yaw])
    }
}