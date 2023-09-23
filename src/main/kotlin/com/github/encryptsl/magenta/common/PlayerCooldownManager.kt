package com.github.encryptsl.magenta.common

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.PlayerAccount
import org.bukkit.entity.Player
import java.time.Duration
import java.time.Instant


class PlayerCooldownManager(player: Player, magenta: Magenta, private val type: String) {

    private val playerAccount = PlayerAccount(magenta, player.uniqueId)

    fun setCooldown(duration: Duration?) {
        playerAccount.getAccount().set("timestamps.$type", Instant.now().plus(duration))
        playerAccount.save()
        playerAccount.reload()
    }

    // Check if cooldown has expired
    fun hasCooldown(): Boolean {
        val cooldown: Instant = Instant.ofEpochSecond(playerAccount.getAccount().getLong("timestamps.$type"))
        return Instant.now().isBefore(cooldown)
    }

    // Remove cooldown
    fun removeCooldown() {
        playerAccount.getAccount().set("timestamps.kits.$type", 0)
        playerAccount.save()
        playerAccount.reload()
    }

    // Get remaining cooldown time
    fun getRemainingCooldown(): Duration {
        val cooldown: Instant = Instant.ofEpochSecond(playerAccount.getAccount().getLong("timestamps.$type"))
        val now = Instant.now()
        return if (now.isBefore(cooldown)) {
            Duration.between(now, cooldown)
        } else {
            Duration.ZERO
        }
    }
}