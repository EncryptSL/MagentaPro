package com.github.encryptsl.magenta.common.database.models

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.database.entity.HomeEntity
import com.github.encryptsl.magenta.common.database.sql.HomeSQL
import com.github.encryptsl.magenta.common.database.tables.HomeTable
import fr.euphyllia.energie.model.SchedulerType
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class HomeModel(private val plugin: Plugin) : HomeSQL {
    override fun createHome(player: Player, location: Location, home: String) {
        Magenta.scheduler.runTask(SchedulerType.ASYNC) {
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
        Magenta.scheduler.runTask(SchedulerType.ASYNC) {
            transaction { HomeTable.deleteWhere { (HomeTable.uuid eq uuid) and (HomeTable.home eq home) } }
        }
    }

    override fun moveHome(uuid: UUID, home: String, location: Location) {
        Magenta.scheduler.runTask(SchedulerType.ASYNC) {
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
        Magenta.scheduler.runTask(SchedulerType.ASYNC) {
            transaction {
                HomeTable.update({ HomeTable.uuid eq uuid and (HomeTable.home eq oldHomeName) }) {
                    it[home] = newHomeName
                }
            }
        }
    }

    override fun setHomeIcon(uuid: UUID, home: String, icon: String) {
        Magenta.scheduler.runTask(SchedulerType.ASYNC) {
            transaction { HomeTable.update({HomeTable.uuid eq uuid and (HomeTable.home eq home)}) {
                it[homeIcon] = icon
            } }
        }
    }

    override fun getHomeExist(uuid: UUID, home: String): Boolean {
        return transaction { !HomeTable.select(HomeTable.home).where(HomeTable.uuid eq uuid and (HomeTable.home eq home)).empty() }
    }

    override fun canSetHome(player: Player): Boolean {
        if (player.hasPermission("magenta.homes.unlimited")) return true

        val section = plugin.config.getConfigurationSection("homes.groups") ?: return false

        val max = section.getKeys(false).filter { player.hasPermission("magenta.homes.$it") }.firstNotNullOf { section.getInt(it) }


        if (max == -1) return true

        return transaction { HomeTable.select(HomeTable.uuid).where(HomeTable.uuid eq player.uniqueId).count() >= max }
    }

    override fun <T> getHome(home: String, columnName: Expression<T>): T {
        return transaction {
            HomeTable.selectAll().where( HomeTable.home eq home).first()[columnName]
        }
    }

    override fun getHomesByOwner(uuid: UUID): List<HomeEntity> {
        return transaction { HomeTable.selectAll().where( HomeTable.uuid eq uuid).mapNotNull{rowResultToHomeEntity(it)} }
    }

    override fun toLocation(player: Player, home: String): Location {
        val homes = getHomesByOwner(player.uniqueId).first { h -> h.homeName == home }

        return Location(Bukkit.getWorld(homes.world), homes.x.toDouble(), homes.y.toDouble(), homes.z.toDouble(), homes.yaw, homes.pitch)
    }

    override fun getHomes(): List<HomeEntity> {
        return transaction { HomeTable.selectAll().mapNotNull {rowResultToHomeEntity(it)} }
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