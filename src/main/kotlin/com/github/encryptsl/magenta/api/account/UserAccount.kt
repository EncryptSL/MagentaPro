package com.github.encryptsl.magenta.api.account

import com.github.encryptsl.magenta.api.config.UniversalConfig
import com.github.encryptsl.magenta.api.votes.MagentaVoteAPI
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.time.Duration
import java.time.Instant
import java.util.*

class UserAccount(private val plugin: Plugin, private val uuid: UUID) : Account {

    private val universalConfig = UniversalConfig(plugin, "/players/$uuid.yml")

    private val voteAPI: MagentaVoteAPI by lazy { MagentaVoteAPI(plugin) }

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

    override fun set(path: String, value: Any?, sync: Boolean) {
        universalConfig.set(path, value, sync)
    }

    override fun set(path: String, list: MutableList<Any>) {
        universalConfig.set(path, list)
    }

    override fun save() {
        universalConfig.save()
    }

    override fun getGameMode(): GameMode {
        return GameMode.valueOf(getAccount().getString("gamemode", "SURVIVAL").toString())
    }

    override fun getFlying(): Boolean {
        return getAccount().getBoolean("flying")
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

    override fun isPlayerIgnored(uuid: UUID): Boolean {
        return getAccount().getStringList("ignore").contains(uuid.toString())
    }

    override fun hasPunish(): Boolean {
        val onlineTime = plugin.config.getBoolean("online-jail-time")

        return if (onlineTime) getOnlineJailedTime() > 0 else hasDelay("jail")
    }

    override fun hasDelay(type: String): Boolean {
        val cooldown: Instant = Instant.ofEpochMilli(getAccount().getLong("timestamps.$type"))
        return Instant.now().isBefore(cooldown)
    }

    override fun getOnlineJailedTime(): Long {
        return getAccount().getLong("timestamps.onlinejail")
    }

    override fun getRemainingJailTime(): Long {
        val onlineTime = plugin.config.getBoolean("online-jail-time")

        return if (onlineTime) getOnlineJailedTime().minus(1) else getRemainingCooldown("jail").seconds
    }

    override fun getRemainingCooldown(type: String): Duration {
        val delay: Instant = Instant.ofEpochMilli(getAccount().getLong("timestamps.$type"))
        val now = Instant.now()
        return if (now.isBefore(delay)) {
            Duration.between(now, delay)
        } else {
            Duration.ZERO
        }
    }

    override fun getVotes(): Int {
        return voteAPI.getPlayerVote(uuid)
    }

    override fun getVotesByService(serviceName: String): Int {
        return voteAPI.getPlayerVote(uuid, serviceName)?.vote ?: 0
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
        return universalConfig.getConfig()
    }
}