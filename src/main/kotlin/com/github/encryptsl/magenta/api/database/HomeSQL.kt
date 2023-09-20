package com.github.encryptsl.magenta.api.database

import com.github.encryptsl.magenta.common.database.entity.HomeEntity
import org.bukkit.Location
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.Expression

interface HomeSQL {

    fun createHome(player: Player, location: Location, home: String)
    fun deleteHome(home: String)
    fun moveHome(home: String, location: Location)
    fun renameHome(oldHomeName: String, newHomeName: String)
    fun getHomeExist(home: String): Boolean

    fun <T> getHome(home: String, columnName: Expression<T>): T

    fun getHomes(): List<HomeEntity>

}