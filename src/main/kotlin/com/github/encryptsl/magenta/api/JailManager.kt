package com.github.encryptsl.magenta.api

import com.github.encryptsl.magenta.Magenta
import org.bukkit.Statistic
import org.bukkit.entity.Player
import java.time.Duration
import java.util.*


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
        account.getAccount().set("timestamps.onlinejail", if (onlineTime) millis else 0)
        account.save()
    }

    fun remainingTime(): Long {
        val onlineTime = magenta.config.getBoolean("online-jail-time")

        return if (onlineTime) getOnlineJailedTime().minus(1) else account.cooldownManager.getRemainingCooldown("jail").seconds
    }

}