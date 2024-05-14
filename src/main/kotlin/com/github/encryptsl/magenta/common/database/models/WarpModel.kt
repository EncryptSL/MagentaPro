package com.github.encryptsl.magenta.common.database.models

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.Permissions
import com.github.encryptsl.magenta.common.database.entity.WarpEntity
import com.github.encryptsl.magenta.common.database.sql.WarpSQL
import com.github.encryptsl.magenta.common.database.tables.HomeTable
import com.github.encryptsl.magenta.common.database.tables.WarpTable
import fr.euphyllia.energie.model.SchedulerType
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID
import java.util.concurrent.CompletableFuture

class WarpModel(private val plugin: Plugin) : WarpSQL {

    override fun creteWarp(player: Player, location: Location, warpName: String) {
        Magenta.scheduler.runTask(SchedulerType.ASYNC) {
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
        Magenta.scheduler.runTask(SchedulerType.ASYNC) {
            transaction { WarpTable.deleteWhere { WarpTable.warpName eq warpName } }
        }
    }

    override fun deleteWarp(uuid: UUID, warpName: String) {
        Magenta.scheduler.runTask(SchedulerType.ASYNC) {
            transaction { WarpTable.deleteWhere { (WarpTable.uuid eq uuid.toString()) and (WarpTable.warpName eq warpName) } }
        }
    }

    override fun moveWarp(warpName: String, location: Location) {
        Magenta.scheduler.runTask(SchedulerType.ASYNC) {
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
        Magenta.scheduler.runTask(SchedulerType.ASYNC) {
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
        Magenta.scheduler.runTask(SchedulerType.ASYNC) {
            transaction {
                WarpTable.update({ WarpTable.warpName eq oldWarpName }) {
                    it[warpName] = newWarpName
                }
            }
        }
    }

    override fun renameWarp(uuid: UUID, oldWarpName: String, newWarpName: String) {
        Magenta.scheduler.runTask(SchedulerType.ASYNC) {
            transaction {
                WarpTable.update({ (WarpTable.uuid eq uuid.toString()) and (WarpTable.warpName eq oldWarpName) }) {
                    it[warpName] = newWarpName
                }
            }
        }
    }

    override fun setWarpIcon(uuid: UUID, warpName: String, icon: String) {
        Magenta.scheduler.runTask(SchedulerType.ASYNC) {
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
        future.completeAsync { boolean }
        return future
    }

    override fun canSetWarp(player: Player): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()

        if (player.hasPermission(Permissions.WARPS_UNLIMITED)) {
            return CompletableFuture.completedFuture<Boolean>(false)
        }

        val section = plugin.config.getConfigurationSection("warps.groups") ?: return CompletableFuture.completedFuture<Boolean>(false)

        val max = section.getKeys(false).filter { player.hasPermission(Permissions.WARPS_LIMIT.format("$it")) }.map { section.getInt(it) }.first()

        if (max == -1) return CompletableFuture.completedFuture<Boolean>(false)

        val boolean = transaction { HomeTable.select(HomeTable.uuid).where(HomeTable.uuid eq player.uniqueId).count() < max }

        return future.completeAsync { boolean }
    }

    override fun getWarp(warpName: String): CompletableFuture<WarpEntity> {
        val future = CompletableFuture<WarpEntity>()
        val data = transaction {
            val warp = WarpTable.selectAll().where(WarpTable.warpName eq warpName).first()
            return@transaction rowResultToWarpEntity(warp)
        }
        future.completeAsync { data }
        return future
    }

    override fun toLocation(warpName: String): Location {
        val rowResult = getWarp(warpName).join()

        return Location(Bukkit.getWorld(rowResult.world), rowResult.x.toDouble(),
            rowResult.y.toDouble(), rowResult.z.toDouble(), rowResult.yaw, rowResult.pitch)
    }

    override fun getWarpsByOwner(uuid: UUID): CompletableFuture<List<WarpEntity>> {
        val future = CompletableFuture<List<WarpEntity>>()
        val map = transaction { WarpTable.selectAll().where(WarpTable.uuid eq uuid.toString()).mapNotNull { rowResultToWarpEntity(it) } }
        future.completeAsync { map }
        return future
    }

    override fun getWarps(): CompletableFuture<List<WarpEntity>> {
        val future = CompletableFuture<List<WarpEntity>>()
        val map = transaction { WarpTable.selectAll().mapNotNull {rowResultToWarpEntity(it)} }
        future.completeAsync { map }
        return future
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