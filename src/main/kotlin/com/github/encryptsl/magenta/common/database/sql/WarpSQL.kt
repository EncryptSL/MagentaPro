package com.github.encryptsl.magenta.common.database.sql

import com.github.encryptsl.magenta.common.database.entity.WarpEntity
import org.bukkit.Location
import org.bukkit.entity.Player

interface WarpSQL {
    fun creteWarp(player: Player, location: Location, warpName: String)
    fun deleteWarp(warpName: String)

    fun deleteWarp(player: Player, warpName: String)
    fun moveWarp(warpName: String, location: Location)
    fun moveWarp(player: Player, warpName: String, location: Location)
    fun renameWarp(oldWarpName: String, newWarpName: String)
    fun renameWarp(player: Player, oldWarpName: String, newWarpName: String)
    fun getWarpExist(warpName: String): Boolean
    fun canSetWarp(player: Player): Boolean
    fun getWarp(warpName: String): WarpEntity
    fun getWarps(): List<WarpEntity>
}