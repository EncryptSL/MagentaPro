package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.jail.JailCheckEvent
import com.github.encryptsl.magenta.common.utils.ModernText
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class JailCheckListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onJailWhileJoin(event: JailCheckEvent) {
        val player = event.player
        val user = magenta.user.getUser(player.uniqueId)

        if (user.jailManager.hasPunish() || user.getAccount().getBoolean("jailed")) {
            val jailSection = magenta.jailConfig.getJail().getConfigurationSection("jails") ?: return
            val randomJail = jailSection.getKeys(false).random()
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.jail.success.jailed")))

            magenta.schedulerMagenta.doSync(magenta) {
                player.teleport(user.jailManager.getJailLocation(randomJail))
            }
            event.isCancelled = true
        } else {
            event.isCancelled = false
        }
    }

}