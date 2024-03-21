package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.jail.JailCheckEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class JailCheckListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onJailWhileJoin(event: JailCheckEvent) {
        val player = event.player
        val user = magenta.user.getUser(player.uniqueId)

        if (user.isJailed()) {
            val jailSection = magenta.jailConfig.getConfig().getConfigurationSection("jails") ?: return
            val randomJail = jailSection.getKeys(false).random()
            player.sendMessage(magenta.localeConfig.translation("magenta.command.jail.success.jailed"))

            player.teleport(magenta.jailManager.getJailLocation(randomJail))

            event.isCancelled = true
        } else {
            event.isCancelled = false
        }
    }

}