package com.github.encryptsl.magenta.api.account

import org.bukkit.Location
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

interface IAccount {

    fun createDefaultData(player: Player)

    fun saveLastLocation(player: Player)
    fun saveQuitData(player: Player)

    fun set(path: String, value: Any?)

    fun set(path: String, list: MutableList<Any>)

    fun save()
    fun isJailed(): Boolean
    fun isSocialSpy(): Boolean
    fun isAfk(): Boolean

    fun getVotifierRewards(): MutableList<String>

    fun getLastLocation(): Location
    fun getAccount(): FileConfiguration
}