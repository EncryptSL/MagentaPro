package com.github.encryptsl.magenta.common.database.sql

import com.github.encryptsl.magenta.common.database.entity.HomeEntity
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture

interface HomeSQL {

    fun createHome(player: Player, location: Location, home: String)
    fun deleteHome(uuid: UUID, home: String)
    fun moveHome(uuid: UUID, home: String, location: Location)
    fun renameHome(uuid: UUID, oldHomeName: String, newHomeName: String)
    fun setHomeIcon(uuid: UUID, home: String, icon: String)
    fun getHomeExist(uuid: UUID, home: String): CompletableFuture<Boolean>

    fun canSetHome(player: Player): CompletableFuture<Boolean>

    fun getHome(home: String): CompletableFuture<HomeEntity>
    fun getHomeByNameAndUUID(uuid: UUID, home: String): CompletableFuture<HomeEntity>

    fun getHomesByOwner(uuid: UUID): CompletableFuture<List<HomeEntity>>

    fun toLocation(player: Player, home: String): Location

    fun getHomes():  CompletableFuture<List<HomeEntity>>

}