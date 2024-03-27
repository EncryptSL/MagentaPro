package com.github.encryptsl.magenta.api.account.models

import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.time.Duration
import java.time.Instant
import java.util.*

class UserAccountImpl(private val plugin: Plugin, uuid: UUID) : UserAccountAbstract(uuid, plugin) {

    override fun createDefaultData(player: Player) {
        getAccount().set("teleportenabled", true)
        getAccount().set("godmode", false)
        getAccount().set("jailed", false)
        getAccount().set("afk", false)
        getAccount().set("ip-address", player.address.address.hostAddress)
        getAccount().set("socialspy", false)
        getAccount().set("timestamps.lastteleport", 0)
        getAccount().set("timestamps.lastheal", 0)
        getAccount().set("timestamps.jail", 0)
        getAccount().set("timestamps.onlinejail", 0)
        getAccount().set("timestamps.logout", 0)
        getAccount().set("timestamps.login", System.currentTimeMillis())
        getAccount().set("lastlocation.world-name", player.world.name)
        getAccount().set("lastlocation.x", player.location.x)
        getAccount().set("lastlocation.y", player.location.y)
        getAccount().set("lastlocation.z", player.location.z)
        getAccount().set("lastlocation.yaw", player.location.yaw)
        getAccount().set("lastlocation.pitch", player.location.pitch)
        save()
    }

    override fun saveLastLocation(player: Player) {
        if (isJailed() || hasPunish()) return
        getAccount().set("lastlocation.world-name", player.world.name)
        getAccount().set("lastlocation.x", player.location.x)
        getAccount().set("lastlocation.y", player.location.y)
        getAccount().set("lastlocation.z", player.location.z)
        getAccount().set("lastlocation.yaw", player.location.yaw)
        getAccount().set("lastlocation.pitch", player.location.pitch)
        save()
    }

    override fun saveQuitData(player: Player) {
        getAccount().set("timestamps.logout", System.currentTimeMillis())
        save()
    }

    override fun setJailTimeout(seconds: Long) {
        setDelay(Duration.ofSeconds(seconds), "jail")
    }

    override fun setOnlineTime(millis: Long) {
        val onlineTime = plugin.config.getBoolean("online-jail-time")
        set("timestamps.onlinejail", if (onlineTime) millis else 0)
    }

    override fun setDelay(duration: Duration?, type: String) {
        set("timestamps.$type", Instant.now().plus(duration).toEpochMilli())
    }

    override fun resetDelay(type: String) {
        set("timestamps.$type", 0)
    }
}