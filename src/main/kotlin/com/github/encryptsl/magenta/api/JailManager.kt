package com.github.encryptsl.magenta.api

import com.github.encryptsl.magenta.Magenta
import org.bukkit.Statistic
import org.bukkit.entity.Player
import java.time.Duration
import java.util.*


class JailManager(private val magenta: Magenta, uuid: UUID) {

    private val account = PlayerAccount(magenta, uuid)


    private fun getOnlineJailedTime(): Long {
        return (account.getAccount().getLong("timestamps.onlinejail"))
    }


    private fun getOnlineJailExpireTime(player: Player): Long {
        return (account.getAccount().getLong("timestamps.onlinejail") - player.getStatistic(Statistic.PLAY_ONE_MINUTE)) * 50 + System.currentTimeMillis()
    }

    fun hasPunish(player: Player): Boolean {
        if (magenta.config.getBoolean("online-jail-time")) {
            if (getOnlineJailedTime() > 0) {
                return getOnlineJailedTime() > player.getStatistic(Statistic.PLAY_ONE_MINUTE)
            }
        }

        return account.cooldownManager.hasCooldown("jail")
    }

    fun setJailTimeout(seconds: Long) {
        account.cooldownManager.setCooldown(Duration.ofSeconds(seconds), "jail")
        account.save()
    }

    fun setOnlineTime(millis: Long) {
        val onlineTime = magenta.config.getBoolean("online-jail-time")
        val time = if (onlineTime) millis / 50 else 0
        account.getAccount().set("timestamps.onlinejail", time)
        account.save()
    }

    fun remainingTime(player: Player): Long {
        if (magenta.config.getBoolean("online-jail-time")) {
            return getOnlineJailExpireTime(player)
        }

        return account.cooldownManager.getRemainingCooldown("jail").seconds
    }

}