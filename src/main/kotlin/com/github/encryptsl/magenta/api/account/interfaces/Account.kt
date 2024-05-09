package com.github.encryptsl.magenta.api.account.interfaces

import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import java.time.Duration
import java.util.*

interface Account {
    fun createDefaultData(player: Player)
    fun saveLastLocation(player: Player)
    fun saveQuitData(player: Player)
    fun addToIgnore(uuid: UUID)
    fun removeIgnoredPlayer(uuid: UUID)
    fun set(path: String, value: Any?)
    fun set(path: MutableMap<String, Any>)
    fun set(path: String, list: MutableList<Any>)
    fun setDelay(duration: Duration?, type: String)
    fun resetDelay(type: String)
    fun setJailTimeout(seconds: Long)
    fun setOnlineTime(millis: Long)
    fun save()
    fun isJailed(): Boolean
    fun isSocialSpy(): Boolean
    fun isAfk(): Boolean
    fun isVanished(): Boolean
    fun isPlayerIgnored(uuid: UUID): Boolean
    fun getGameMode(): GameMode
    fun getFlying(): Boolean
    fun getOnlineJailedTime(): Long
    fun getRemainingJailTime(): Long
    fun getRemainingCooldown(type: String): Duration
    fun hasPunish(): Boolean
    fun hasDelay(type: String): Boolean
    fun getVotes(): Int
    fun getVotesByService(serviceName: String): Int
    fun getVotifierRewards(): MutableList<String>
    fun getLastLocation(): Location
    fun getPlayer(): Player?
    fun getOfflinePlayer(): OfflinePlayer?
    fun getAccount(): FileConfiguration
}