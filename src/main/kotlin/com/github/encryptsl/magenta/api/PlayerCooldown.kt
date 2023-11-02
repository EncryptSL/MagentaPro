package com.github.encryptsl.magenta.api

import com.github.encryptsl.magenta.api.account.PlayerAccount
import java.time.Duration
import java.time.Instant
import java.util.*

class PlayerCooldown(val uuid: UUID, private val playerAccount: PlayerAccount) {

    fun setDelay(duration: Duration?, type: String) {
        playerAccount.getAccount().set("timestamps.$type", Instant.now().plus(duration).toEpochMilli())

    }

    // Check if cooldown has expired
    fun hasDelay(type: String): Boolean {
        val cooldown: Instant = Instant.ofEpochMilli(playerAccount.getAccount().getLong("timestamps.$type"))
        return Instant.now().isBefore(cooldown)
    }

    // Remove cooldown
    fun removeDelay(type: String) {
        playerAccount.set("timestamps.$type", 0)
    }

    // Get remaining cooldown time
    fun getRemainingDelay(type: String): Duration {
        val delay: Instant = Instant.ofEpochMilli(playerAccount.getAccount().getLong("timestamps.$type"))
        val now = Instant.now()
        return if (now.isBefore(delay)) {
            Duration.between(now, delay)
        } else {
            Duration.ZERO
        }
    }

}