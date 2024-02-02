package com.github.encryptsl.magenta.api.account

import com.github.encryptsl.magenta.api.PlayerCooldown
import com.github.encryptsl.magenta.api.scheduler.SchedulerMagenta
import com.github.encryptsl.magenta.common.utils.ConfigUtil
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.time.Duration
import java.util.*

class UserAccount(private val plugin: Plugin, private val uuid: UUID) : Account {

    private val configUtil = ConfigUtil(plugin,"/players/$uuid.yml")
    private val schedulerMagenta: SchedulerMagenta by lazy { SchedulerMagenta() }

    val cooldownManager: PlayerCooldown by lazy { PlayerCooldown(uuid, this) }

    override fun createDefaultData(player: Player) {
        schedulerMagenta.doAsync(plugin) {
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
    }

    override fun saveLastLocation(player: Player) {
        schedulerMagenta.doAsync(plugin) {
            getAccount().set("lastlocation.world-name", player.world.name)
            getAccount().set("lastlocation.x", player.location.x)
            getAccount().set("lastlocation.y", player.location.y)
            getAccount().set("lastlocation.z", player.location.z)
            getAccount().set("lastlocation.yaw", player.location.yaw)
            getAccount().set("lastlocation.pitch", player.location.pitch)
            save()
        }
    }
    override fun saveQuitData(player: Player) {
        schedulerMagenta.doAsync(plugin) {
            getAccount().set("timestamps.logout", System.currentTimeMillis())
            save()
        }
    }

    override fun setJailTimeout(seconds: Long) {
        cooldownManager.setDelay(Duration.ofSeconds(seconds), "jail")
        save()
    }

    override fun setOnlineTime(millis: Long) {
        val onlineTime = plugin.config.getBoolean("online-jail-time")
        set("timestamps.onlinejail", if (onlineTime) millis else 0)
        save()
    }

    override fun set(path: String, value: Any?) {
        schedulerMagenta.doAsync(plugin) {
            getAccount().set(path, value)
            save()
        }
    }

    override fun set(path: String, list: MutableList<Any>) {
        schedulerMagenta.doAsync(plugin) {
            list.forEach { item ->
                getAccount().set(path, item)
            }
            save()
        }
    }

    override fun save() {
        configUtil.save()
    }

    override fun isJailed(): Boolean {
        return getAccount().getBoolean("jailed") || hasPunish()
    }

    override fun isSocialSpy(): Boolean {
        return getAccount().getBoolean("socialspy")
    }

    override fun isAfk(): Boolean {
        return getAccount().getBoolean("afk")
    }

    override fun isVanished(): Boolean {
        return getAccount().getBoolean("vanished")
    }

    override fun hasPunish(): Boolean {
        val onlineTime = plugin.config.getBoolean("online-jail-time")

        return if (onlineTime) getOnlineJailedTime() > 0 else cooldownManager.hasDelay("jail")
    }

    override fun getOnlineJailedTime(): Long {
        return getAccount().getLong("timestamps.onlinejail")
    }

    override fun getRemainingTime(): Long {
        val onlineTime = plugin.config.getBoolean("online-jail-time")

        return if (onlineTime) getOnlineJailedTime().minus(1) else cooldownManager.getRemainingDelay("jail").seconds
    }

    override fun getVotifierRewards(): MutableList<String> {
        return getAccount().getStringList("votifier.rewards")
    }

    override fun getLastLocation(): Location {
        val world = getAccount().getString("lastlocation.world-name").toString()
        val x = getAccount().getDouble("lastlocation.x")
        val y = getAccount().getDouble("lastlocation.y")
        val z = getAccount().getDouble("lastlocation.z")
        val yaw = getAccount().getString("lastlocation.yaw").toString().toFloat()
        val pitch = getAccount().getString("lastlocation.pitch").toString().toFloat()
        return Location(Bukkit.getWorld(world), x, y, z, yaw, pitch)
    }

    override fun getAccount(): FileConfiguration {
        return configUtil.getConfig()
    }
}