package com.github.encryptsl.magenta.common.database.models

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.Permissions
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
import java.util.*
import java.util.concurrent.CompletableFuture

class WarpModel(private val plugin: Plugin) : WarpSQL {

    override fun creteWarp(player: Player, location: Location, warpName: String) {
        Magenta.scheduler.impl.runAsync {
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
        Magenta.scheduler.impl.runAsync {
            transaction { WarpTable.deleteWhere { WarpTable.warpName eq warpName } }
        }
    }

    override fun deleteWarp(uuid: UUID, warpName: String) {
        Magenta.scheduler.impl.runAsync {
            transaction { WarpTable.deleteWhere { (WarpTable.uuid eq uuid.toString()) and (WarpTable.warpName eq warpName) } }
        }
    }

    override fun moveWarp(warpName: String, location: Location) {
        Magenta.scheduler.impl.runAsync {
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

    override fun moveWarp(uuid: UUID, warpName: String, location: Location) {
        Magenta.scheduler.impl.runAsync {
            transaction { WarpTable.update( { (WarpTable.uuid eq uuid.toString()) and (WarpTable.warpName eq warpName) }) {
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
        Magenta.scheduler.impl.runAsync {
            transaction {
                WarpTable.update({ WarpTable.warpName eq oldWarpName }) {
                    it[warpName] = newWarpName
                }
            }
        }
    }

    override fun renameWarp(uuid: UUID, oldWarpName: String, newWarpName: String) {
        Magenta.scheduler.impl.runAsync {
            transaction {
                WarpTable.update({ (WarpTable.uuid eq uuid.toString()) and (WarpTable.warpName eq oldWarpName) }) {
                    it[warpName] = newWarpName
                }
            }
        }
    }

    override fun setWarpIcon(uuid: UUID, warpName: String, icon: String) {
        Magenta.scheduler.impl.runAsync {
            transaction {
                WarpTable.update({(WarpTable.uuid eq uuid.toString()) and (WarpTable.warpName eq warpName)}) {
                    it[warpIcon] = icon
                }
            }
        }
    }


    override fun getWarpExist(warpName: String): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        val boolean = transaction { !WarpTable.select(WarpTable.warpName).where(WarpTable.warpName eq warpName).empty() }
        return future.completeAsync { boolean }
    }

    override fun canSetWarp(player: Player): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()

        val createdWarpsCount = transaction { HomeTable.select(HomeTable.uuid).where(HomeTable.uuid eq player.uniqueId).count() }

        if (player.hasPermission(Permissions.WARPS_UNLIMITED)) {
            return CompletableFuture.completedFuture(true)
        }

        val section = plugin.config.getConfigurationSection("warps.groups") ?: return CompletableFuture.completedFuture(false)

        val max = section.getKeys(false).filter { player.hasPermission(Permissions.WARPS_LIMIT.format(it)) }.map { section.getInt(it) }.first()

        if (max == -1) return CompletableFuture.completedFuture(true)

        return future.completeAsync { !(createdWarpsCount >= max) }
    }

    override fun getWarpByName(warpName: String): CompletableFuture<WarpEntity> {
        val future = CompletableFuture<WarpEntity>()
        transaction {
            val warp = WarpTable.selectAll().where(WarpTable.warpName eq warpName).firstOrNull()
            if (warp == null) {
                future.completeExceptionally(RuntimeException("Warp not found !"))
            } else {
                future.completeAsync { rowResultToWarpEntity(warp) }
            }
        }
        return future
    }

    override fun toLocation(warpName: String): Location {
        return getWarpByName(warpName).thenApply { rowResult ->
            Location(Bukkit.getWorld(rowResult.world), rowResult.x.toDouble(),
                rowResult.y.toDouble(), rowResult.z.toDouble(), rowResult.yaw, rowResult.pitch)
        }.join()
    }

    override fun getWarpsByOwner(uuid: UUID): CompletableFuture<List<WarpEntity>> {
        val future = CompletableFuture<List<WarpEntity>>()
        val map = transaction { WarpTable.selectAll().where(WarpTable.uuid eq uuid.toString()).mapNotNull { rowResultToWarpEntity(it) } }
        return future.completeAsync { map }
    }

    override fun getWarps(): CompletableFuture<List<WarpEntity>> {
        val future = CompletableFuture<List<WarpEntity>>()
        val map = transaction { WarpTable.selectAll().mapNotNull {rowResultToWarpEntity(it)} }
        return future.completeAsync { map }
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