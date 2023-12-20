package com.github.encryptsl.magenta.common.database.sql

import org.bukkit.Location

interface RaidsSQL {
    fun createArena(bossName: String, location: Location)
    fun setName(bossName: String, newBossName: String)
    fun setPrice(bossName: String, price: Int)
    fun moveArena(bossName: String, location: Location)
    fun deleteArena(bossName: String)
}