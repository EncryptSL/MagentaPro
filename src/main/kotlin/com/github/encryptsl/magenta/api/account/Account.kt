package com.github.encryptsl.magenta.api.account

import org.bukkit.Location
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

interface Account {

    fun createDefaultData(player: Player)
    fun saveLastLocation(player: Player)
    fun saveQuitData(player: Player)
    fun set(path: String, value: Any?)
    fun set(path: String, list: MutableList<Any>)
    fun setJailTimeout(seconds: Long)
    fun setOnlineTime(millis: Long)
    fun save()
    fun isJailed(): Boolean
    fun isSocialSpy(): Boolean
    fun isAfk(): Boolean
    fun isVanished(): Boolean

    fun getOnlineJailedTime(): Long

    fun getRemainingTime(): Long
    fun hasPunish(): Boolean

    fun getVotes(): Int
    fun getVotesByService(serviceName: String): Int
    fun getVotifierRewards(): MutableList<String>
    fun getLastLocation(): Location
    fun getAccount(): FileConfiguration
}