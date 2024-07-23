package com.github.encryptsl.magenta.common.database.models

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.Permissions
import com.github.encryptsl.magenta.common.database.entity.HomeEntity
import com.github.encryptsl.magenta.common.database.sql.HomeSQL
import com.github.encryptsl.magenta.common.database.tables.HomeTable
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import java.util.concurrent.CompletableFuture

class HomeModel(private val plugin: Plugin) : HomeSQL {
    override fun createHome(player: Player, location: Location, home: String) {
        Magenta.scheduler.impl.runAsync {
            transaction {
                HomeTable.insertIgnore {
                    it[username] = player.name
                    it[uuid] = player.uniqueId
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
    }

    override fun deleteHome(uuid: UUID, home: String) {
        Magenta.scheduler.impl.runAsync {
            transaction { HomeTable.deleteWhere { (HomeTable.uuid eq uuid) and (HomeTable.home eq home) } }
        }
    }

    override fun moveHome(uuid: UUID, home: String, location: Location) {
        Magenta.scheduler.impl.runAsync {
            transaction { HomeTable.update( {  (HomeTable.uuid eq uuid) and (HomeTable.home eq home) }) {
                it[world] = location.world.name
                it[x] = location.x.toInt()
                it[y] = location.y.toInt()
                it[z] = location.z.toInt()
                it[yaw] = location.yaw
                it[pitch] = location.pitch
            } }
        }
    }

    override fun renameHome(uuid: UUID, oldHomeName: String, newHomeName: String) {
        Magenta.scheduler.impl.runAsync {
            transaction {
                HomeTable.update({ HomeTable.uuid eq uuid and (HomeTable.home eq oldHomeName) }) {
                    it[home] = newHomeName
                }
            }
        }
    }

    override fun setHomeIcon(uuid: UUID, home: String, icon: String) {
        Magenta.scheduler.impl.runAsync {
            transaction { HomeTable.update({HomeTable.uuid eq uuid and (HomeTable.home eq home)}) {
                it[homeIcon] = icon
            } }
        }
    }

    override fun getHomeExist(uuid: UUID, home: String): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        val boolean = transaction { !HomeTable.select(HomeTable.home).where(HomeTable.uuid eq uuid and (HomeTable.home eq home)).empty() }
        return future.completeAsync { boolean }
    }

    override fun canSetHome(player: Player):  CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()

        val createdHomesCount = transaction { HomeTable.select(HomeTable.uuid).where(HomeTable.uuid eq player.uniqueId).count() }

        if (player.hasPermission(Permissions.HOME_UNLIMITED))
            return future.completeAsync { true }

        val section = plugin.config.getConfigurationSection("homes.groups") ?: return future.completeAsync { false }

        val max = section.getKeys(false).filter { player.hasPermission("magenta.homes.$it") }.firstNotNullOf { section.getInt(it) }

        if (max == -1) return future.completeAsync { true }

        return future.completeAsync { !(createdHomesCount >= max) }
    }

    override fun getHome(home: String): CompletableFuture<HomeEntity> {
        val future = CompletableFuture<HomeEntity>()
        val homeRow = transaction { HomeTable.selectAll().where( HomeTable.home eq home).first() }
        return future.completeAsync { rowResultToHomeEntity(homeRow) }
    }

    override fun getHomeByNameAndUUID(uuid: UUID, home: String): CompletableFuture<HomeEntity> {
        val future = CompletableFuture<HomeEntity>()
        val homeRow = transaction { HomeTable.selectAll().where( HomeTable.home eq home and (HomeTable.uuid eq uuid)).firstOrNull() }
        if (homeRow == null) {
            future.completeExceptionally(RuntimeException())
        } else {
            future.completeAsync { rowResultToHomeEntity(homeRow) }
        }
        return future
    }

    override fun getHomesByOwner(uuid: UUID): CompletableFuture<List<HomeEntity>> {
        val future = CompletableFuture<List<HomeEntity>>()

        val homes = transaction { HomeTable.selectAll().where(HomeTable.uuid eq uuid).mapNotNull{rowResultToHomeEntity(it)} }
        return future.completeAsync { homes }
    }

    override fun toLocation(player: Player, home: String): Location {
        val homes = getHomesByOwner(player.uniqueId).join().first { h -> h.homeName == home }
        return Location(Bukkit.getWorld(homes.world), homes.x.toDouble(), homes.y.toDouble(), homes.z.toDouble(), homes.yaw, homes.pitch)
    }

    override fun getHomes(): CompletableFuture<List<HomeEntity>> {
        val future = CompletableFuture<List<HomeEntity>>()
        val homes = transaction { HomeTable.selectAll().mapNotNull {rowResultToHomeEntity(it)} }
        return future.completeAsync { homes }
    }

    private fun rowResultToHomeEntity(row: ResultRow): HomeEntity {
        return HomeEntity(
            row[HomeTable.username],
            row[HomeTable.uuid],
            row[HomeTable.home],
            row[HomeTable.homeIcon],
            row[HomeTable.world],
            row[HomeTable.x],
            row[HomeTable.y],
            row[HomeTable.z],
            row[HomeTable.pitch],
            row[HomeTable.pitch]
        )
    }
}