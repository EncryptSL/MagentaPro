package com.github.encryptsl.magenta.api.account.models

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.account.interfaces.Account
import com.github.encryptsl.magenta.api.config.UniversalConfig
import com.github.encryptsl.magenta.api.votes.MagentaVoteAPI
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.configuration.file.FileConfiguration
import java.time.Duration
import java.time.Instant
import java.util.*

abstract class UserAccountAbstract(private val uuid: UUID) : Account {

    private val universalConfig = UniversalConfig("/players/$uuid.yml")
    private val voteAPI: MagentaVoteAPI by lazy { MagentaVoteAPI() }

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
        val onlineTime = Magenta.instance.config.getBoolean("online-jail-time")

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
        val onlineTime = Magenta.instance.config.getBoolean("online-jail-time")

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
        return universalConfig.getConfig().getLocation("lastlocation") ?: throw Exception("Something bad with last saved location")
    }

    override fun set(path: String, value: Any?, sync: Boolean) {
        universalConfig.set(path, value, sync)
    }

    override fun set(path: MutableMap<String, Any>, sync: Boolean) {
        for (i in path) { universalConfig.set(i.key, i.value, sync) }
    }

    override fun set(path: String, list: MutableList<Any>) {
        universalConfig.set(path, list)
    }

    override fun save() {
        universalConfig.save()
    }

    override fun getAccount(): FileConfiguration {
        return universalConfig.getConfig()
    }
}