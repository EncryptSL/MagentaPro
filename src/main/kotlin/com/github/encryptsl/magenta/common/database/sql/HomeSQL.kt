package com.github.encryptsl.magenta.common.database.sql

import com.github.encryptsl.magenta.common.database.entity.HomeEntity
import org.bukkit.Location
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.Expression
import java.util.*

interface HomeSQL {

    fun createHome(player: Player, location: Location, home: String)
    fun deleteHome(uuid: UUID, home: String)
    fun moveHome(uuid: UUID, home: String, location: Location)
    fun renameHome(uuid: UUID, oldHomeName: String, newHomeName: String)
    fun setHomeIcon(uuid: UUID, home: String, icon: String)
    fun getHomeExist(uuid: UUID, home: String): Boolean

    fun canSetHome(player: Player): Boolean

    fun <T> getHome(home: String, columnName: Expression<T>): T

    fun getHomesByOwner(uuid: UUID): List<HomeEntity>

    fun toLocation(player: Player, home: String): Location

    fun getHomes(): List<HomeEntity>

}