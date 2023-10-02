package com.github.encryptsl.magenta.api

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.account.PlayerAccount
import org.bukkit.Bukkit
import org.bukkit.Location
import java.time.Duration


class JailManager(private val magenta: Magenta, private val account: PlayerAccount) {

    private fun getOnlineJailedTime(): Long {
        return (account.getAccount().getLong("timestamps.onlinejail"))
    }

    fun hasPunish(): Boolean {
        val onlineTime = magenta.config.getBoolean("online-jail-time")

        return if (onlineTime) getOnlineJailedTime() > 0 else account.cooldownManager.hasCooldown("jail")
    }

    fun setJailTimeout(seconds: Long) {
        account.cooldownManager.setCooldown(Duration.ofSeconds(seconds), "jail")
        account.save()
    }

    fun setOnlineTime(millis: Long) {
        val onlineTime = magenta.config.getBoolean("online-jail-time")
        account.set("timestamps.onlinejail", if (onlineTime) millis else 0)
    }

    fun remainingTime(): Long {
        val onlineTime = magenta.config.getBoolean("online-jail-time")

        return if (onlineTime) getOnlineJailedTime().minus(1) else account.cooldownManager.getRemainingCooldown("jail").seconds
    }

    fun getJailLocation(jailName: String): Location {
        return Location(Bukkit.getWorld(magenta.jailConfig.getJail().getString("jails.${jailName}.location.world").toString()),
            magenta.jailConfig.getJail().getDouble("jails.${jailName}.location.x"),
            magenta.jailConfig.getJail().getDouble("jails.${jailName}.location.y"),
            magenta.jailConfig.getJail().getDouble("jails.${jailName}.location.z"),
            magenta.jailConfig.getJail().getInt("jails.${jailName}.location.yaw").toFloat(),
            magenta.jailConfig.getJail().getInt("jails.${jailName}.location.pitch").toFloat()
        )
    }

}