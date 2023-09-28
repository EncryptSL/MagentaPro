package com.github.encryptsl.magenta.api

import java.time.Duration
import java.time.Instant
import java.util.*

class PlayerCooldown(val uuid: UUID, val playerAccount: PlayerAccount) {

    fun setCooldown(duration: Duration?, type: String) {
        playerAccount.getAccount().set("timestamps.$type", Instant.now().plus(duration).toEpochMilli())

    }

    // Check if cooldown has expired
    fun hasCooldown(type: String): Boolean {
        val cooldown: Instant = Instant.ofEpochMilli(playerAccount.getAccount().getLong("timestamps.$type"))
        return Instant.now().isBefore(cooldown)
    }

    // Remove cooldown
    fun removeCooldown(type: String) {
        playerAccount.getAccount().set("timestamps.$type", 0)
    }

    // Get remaining cooldown time
    fun getRemainingCooldown(type: String): Duration {
        val cooldown: Instant = Instant.ofEpochMilli(playerAccount.getAccount().getLong("timestamps.$type"))
        val now = Instant.now()
        return if (now.isBefore(cooldown)) {
            Duration.between(now, cooldown)
        } else {
            Duration.ZERO
        }
    }

}