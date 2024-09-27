package com.github.encryptsl.magenta.common.database.models

import com.github.encryptsl.magenta.common.Permissions
import com.github.encryptsl.magenta.common.database.entity.WarpEntity
import com.github.encryptsl.magenta.common.database.sql.WarpSQL
import com.github.encryptsl.magenta.common.database.tables.HomeTable
import com.github.encryptsl.magenta.common.database.tables.WarpTable
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import java.util.concurrent.CompletableFuture

class WarpModel(private val plugin: Plugin) : WarpSQL {

    override fun creteWarp(player: Player, location: Location, warpName: String) {
        CompletableFuture.runAsync {
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
        CompletableFuture.runAsync {
            transaction { WarpTable.deleteWhere { WarpTable.warpName eq warpName } }
        }
    }

    override fun deleteWarp(uuid: UUID, warpName: String) {
        CompletableFuture.runAsync {
            transaction { WarpTable.deleteWhere { (WarpTable.uuid eq uuid.toString()) and (WarpTable.warpName eq warpName) } }
        }
    }

    override fun moveWarp(warpName: String, location: Location) {
        CompletableFuture.runAsync {
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
        CompletableFuture.runAsync {
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
        CompletableFuture.runAsync {
            transaction {
                WarpTable.update({ WarpTable.warpName eq oldWarpName }) {
                    it[warpName] = newWarpName
                }
            }
        }
    }

    override fun renameWarp(uuid: UUID, oldWarpName: String, newWarpName: String) {
        CompletableFuture.runAsync {
            transaction {
                WarpTable.update({ (WarpTable.uuid eq uuid.toString()) and (WarpTable.warpName eq oldWarpName) }) {
                    it[warpName] = newWarpName
                }
            }
        }
    }

    override fun setWarpIcon(uuid: UUID, warpName: String, icon: String) {
        CompletableFuture.runAsync {
            transaction {
                WarpTable.update({(WarpTable.uuid eq uuid.toString()) and (WarpTable.warpName eq warpName)}) {
                    it[warpIcon] = icon
                }
            }
        }
    }


    override fun getWarpExist(warpName: String): CompletableFuture<Boolean> {
        val future: CompletableFuture<Boolean> = CompletableFuture.supplyAsync {
            val boolean = transaction { !WarpTable.select(WarpTable.warpName).where(WarpTable.warpName eq warpName).empty() }

            return@supplyAsync boolean
        }

        return future
    }

    override fun canSetWarp(player: Player): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync {
            val createdWarpsCount = transaction { HomeTable.select(HomeTable.uuid).where(HomeTable.uuid eq player.uniqueId).count() }

            if (player.hasPermission(Permissions.WARPS_UNLIMITED)) {
                return@supplyAsync true
            }

            val section = plugin.config.getConfigurationSection("warps.groups") ?: return@supplyAsync false

            val group = section.getKeys(false).firstOrNull { player.hasPermission(Permissions.WARPS_LIMIT.format(it)) }

            if (group == null) {
                return@supplyAsync createdWarpsCount < section.getInt("default")
            } else {
                if (section.getInt(group) == -1) {
                    return@supplyAsync true
                }

                return@supplyAsync createdWarpsCount < section.getInt(group)
            }
        }
    }

    override fun getWarpByName(warpName: String): CompletableFuture<WarpEntity> {
        val future: CompletableFuture<WarpEntity> = CompletableFuture.supplyAsync {
            transaction {
                try {
                    val warp = WarpTable.selectAll().where(WarpTable.warpName eq warpName).first()
                    rowResultToWarpEntity(warp)
                } catch (e : ExposedSQLException) {
                    throw RuntimeException("Warp not found !")
                }
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
        val future: CompletableFuture<List<WarpEntity>> = CompletableFuture.supplyAsync {
            transaction { WarpTable.selectAll().where(WarpTable.uuid eq uuid.toString()).mapNotNull { rowResultToWarpEntity(it) } }
        }

        return future
    }

    override fun getWarps(): CompletableFuture<List<WarpEntity>> {
        val future: CompletableFuture<List<WarpEntity>> = CompletableFuture.supplyAsync {
            transaction { WarpTable.selectAll().mapNotNull {rowResultToWarpEntity(it)} }
        }
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