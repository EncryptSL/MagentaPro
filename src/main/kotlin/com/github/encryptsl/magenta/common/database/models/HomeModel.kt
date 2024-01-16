package com.github.encryptsl.magenta.common.database.models

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.database.entity.HomeEntity
import com.github.encryptsl.magenta.common.database.sql.HomeSQL
import com.github.encryptsl.magenta.common.database.tables.HomeTable
import org.bukkit.Location
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class HomeModel(private val magenta: Magenta) : HomeSQL {
    override fun createHome(player: Player, location: Location, home: String) {
        magenta.schedulerMagenta.doAsync(magenta) {
            transaction {
                HomeTable.insertIgnore {
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
    }

    override fun deleteHome(player: Player, home: String) {
        magenta.schedulerMagenta.doAsync(magenta) {
            transaction { HomeTable.deleteWhere { (uuid eq player.uniqueId.toString()) and (HomeTable.home eq home) } }
        }
    }

    override fun moveHome(player: Player, home: String, location: Location) {
        magenta.schedulerMagenta.doAsync(magenta) {
            transaction { HomeTable.update( {  (HomeTable.uuid eq player.uniqueId.toString()) and (HomeTable.home eq home) }) {
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
        magenta.schedulerMagenta.doAsync(magenta) {
            transaction {
                HomeTable.update({ HomeTable.uuid eq player.uniqueId.toString() and (HomeTable.home eq oldHomeName) }) {
                    it[home] = newHomeName
                }
            }
        }
    }

    override fun getHomeExist(player: Player, home: String): Boolean {
        return transaction { !HomeTable.select(HomeTable.home eq home).empty() }
    }

    override fun canSetHome(player: Player): Boolean {
        if (player.hasPermission("magenta.homes.unlimited")) return true

        val section = magenta.config.getConfigurationSection("homes.groups") ?: return false

        val max = section.getKeys(false).filter { player.hasPermission("magenta.homes.$it") }.firstNotNullOf { section.getInt(it) }


        if (max == -1) return true

        return transaction { HomeTable.select(HomeTable.uuid eq player.uniqueId.toString()).count() >= max }
    }

    override fun <T> getHome(home: String, columnName: Expression<T>): T {
        return transaction {
            HomeTable.select(HomeTable.home eq home).first()[columnName]
        }
    }

    override fun getHomesByOwner(player: Player): List<HomeEntity> {
        return transaction { HomeTable.select { HomeTable.uuid eq player.uniqueId.toString() }.mapNotNull { r ->
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

    override fun getHomes(): List<HomeEntity> {
        return transaction { HomeTable.selectAll().mapNotNull { r ->
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