package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.PlayerAccount
import com.github.encryptsl.magenta.api.events.jail.JailReleaseEvent
import com.github.encryptsl.magenta.common.PlayerCooldownManager
import com.github.encryptsl.magenta.common.utils.ModernText
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class JailReleaseListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onJailRelease(event: JailReleaseEvent) {
        val players: MutableCollection<out Player> = event.players

        magenta.schedulerMagenta.runTaskTimeSync(magenta, {
            players.filter { !PlayerCooldownManager(it.uniqueId, magenta, "jail").hasCooldown() }.forEach { player ->
                val account = PlayerAccount(magenta, player.uniqueId)
                player.sendMessage(ModernText.miniModernText("${player.name} byl propuštěn !"))
                account.getAccount().set("jailed", false)
            }
        }, 20, 20)
    }

}