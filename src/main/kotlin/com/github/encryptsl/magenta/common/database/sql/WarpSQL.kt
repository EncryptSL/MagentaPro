package com.github.encryptsl.magenta.common.database.sql

import com.github.encryptsl.magenta.common.database.entity.WarpEntity
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture

interface WarpSQL {
    fun creteWarp(player: Player, location: Location, warpName: String)
    fun deleteWarp(warpName: String)

    fun deleteWarp(uuid: UUID, warpName: String)
    fun moveWarp(warpName: String, location: Location)
    fun moveWarp(uuid: UUID, warpName: String, location: Location)
    fun renameWarp(oldWarpName: String, newWarpName: String)
    fun renameWarp(uuid: UUID, oldWarpName: String, newWarpName: String)
    fun setWarpIcon(uuid: UUID, warpName: String, icon: String)
    fun getWarpExist(warpName: String): CompletableFuture<Boolean>
    fun canSetWarp(player: Player): CompletableFuture<Boolean>
    fun getWarpByName(warpName: String): CompletableFuture<WarpEntity>
    fun toLocation(warpName: String): Location
    fun getWarpsByOwner(uuid: UUID): CompletableFuture<List<WarpEntity>>
    fun getWarps(): CompletableFuture<List<WarpEntity>>
}