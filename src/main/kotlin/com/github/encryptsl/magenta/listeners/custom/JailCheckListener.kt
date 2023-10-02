package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.account.PlayerAccount
import com.github.encryptsl.magenta.api.events.jail.JailCheckEvent
import com.github.encryptsl.magenta.common.utils.ModernText
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class JailCheckListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onJailWhileJoin(event: JailCheckEvent) {
        val player = event.player
        val playerAccount = PlayerAccount(magenta, player.uniqueId)

        if (playerAccount.jailManager.hasPunish() || playerAccount.getAccount().getBoolean("jailed")) {
            val jailSection = magenta.jailConfig.getJail().getConfigurationSection("jails") ?: return
            val randomJail = jailSection.getKeys(false).random()
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.jail.success.jailed")))

            magenta.schedulerMagenta.runTask(magenta) {
                player.teleport(playerAccount.jailManager.getJailLocation(randomJail))
            }
            event.isCancelled = true
        } else {
            event.isCancelled = false
        }
    }

}