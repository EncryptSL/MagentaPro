package com.github.encryptsl.magenta.common.database.sql

import com.github.encryptsl.magenta.common.database.entity.HomeEntity
import org.bukkit.Location
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.Expression

interface HomeSQL {

    fun createHome(player: Player, location: Location, home: String)
    fun deleteHome(player: Player, home: String)
    fun moveHome(player: Player, home: String, location: Location)
    fun renameHome(player: Player, oldHomeName: String, newHomeName: String)
    fun setHomeIcon(player: Player, home: String, icon: String)
    fun getHomeExist(player: Player, home: String): Boolean

    fun canSetHome(player: Player): Boolean

    fun <T> getHome(home: String, columnName: Expression<T>): T

    fun getHomesByOwner(player: Player): List<HomeEntity>

    fun toLocation(player: Player, home: String): Location

    fun getHomes(): List<HomeEntity>

}