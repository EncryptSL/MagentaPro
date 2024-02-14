package com.github.encryptsl.magenta.common.database.models

import com.github.encryptsl.magenta.api.scheduler.SchedulerMagenta
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

class HomeModel(private val plugin: Plugin) : HomeSQL {
    override fun createHome(player: Player, location: Location, home: String) {
        SchedulerMagenta.doAsync(plugin) {
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

    override fun deleteHome(player: Player, home: String) {
        SchedulerMagenta.doAsync(plugin) {
            transaction { HomeTable.deleteWhere { (uuid eq player.uniqueId) and (HomeTable.home eq home) } }
        }
    }

    override fun moveHome(player: Player, home: String, location: Location) {
        SchedulerMagenta.doAsync(plugin) {
            transaction { HomeTable.update( {  (HomeTable.uuid eq player.uniqueId) and (HomeTable.home eq home) }) {
                it[world] = location.world.name
                it[x] = location.x.toInt()
                it[y] = location.y.toInt()
                it[z] = location.z.toInt()
                it[yaw] = location.yaw
                it[pitch] = location.pitch
            } }
        }
    }

    override fun renameHome(player: Player, oldHomeName: String, newHomeName: String) {
        SchedulerMagenta.doAsync(plugin) {
            transaction {
                HomeTable.update({ HomeTable.uuid eq player.uniqueId and (HomeTable.home eq oldHomeName) }) {
                    it[home] = newHomeName
                }
            }
        }
    }

    override fun setHomeIcon(player: Player, home: String, icon: String) {
        transaction { HomeTable.update({HomeTable.uuid eq player.uniqueId}) {} }
    }

    override fun getHomeExist(player: Player, home: String): Boolean {
        return transaction { !HomeTable.select(HomeTable.home).where(HomeTable.home eq home).empty() }
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

    override fun getHomesByOwner(player: Player): List<HomeEntity> {
        return transaction { HomeTable.selectAll().where( HomeTable.uuid eq player.uniqueId).mapNotNull{rowResultToHomeEntity(it)} }
    }

    override fun toLocation(player: Player, home: String): Location {
        val homes = getHomesByOwner(player).first { h -> h.homeName == home }

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