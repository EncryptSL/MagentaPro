package com.github.encryptsl.magenta.common.database.sql

import com.github.encryptsl.magenta.common.database.entity.WarpEntity
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

interface WarpSQL {
    fun creteWarp(player: Player, location: Location, warpName: String)
    fun deleteWarp(warpName: String)

    fun deleteWarp(uuid: UUID, warpName: String)
    fun moveWarp(warpName: String, location: Location)
    fun moveWarp(uuid: UUID, warpName: String, location: Location)
    fun renameWarp(oldWarpName: String, newWarpName: String)
    fun renameWarp(uuid: UUID, oldWarpName: String, newWarpName: String)
    fun setWarpIcon(uuid: UUID, warpName: String, icon: String)
    fun getWarpExist(warpName: String): Boolean
    fun canSetWarp(player: Player): Boolean
    fun getWarp(warpName: String): WarpEntity
    fun toLocation(warpName: String): Location
    fun getWarpsByOwner(uuid: UUID): List<WarpEntity>
    fun getWarps(): List<WarpEntity>
}