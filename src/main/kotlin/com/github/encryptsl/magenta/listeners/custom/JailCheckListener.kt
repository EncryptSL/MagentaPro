package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.PlayerAccount
import com.github.encryptsl.magenta.api.events.jail.JailCheckEvent
import com.github.encryptsl.magenta.common.PlayerCooldownManager
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class JailCheckListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onJailWhileJoin(event: JailCheckEvent) {
        val player = event.player
        val playerAccount = PlayerAccount(magenta, player.uniqueId)
        val cooldown = PlayerCooldownManager(player.uniqueId, magenta, "jail")

        if (cooldown.hasCooldown() && playerAccount.getAccount().contains("jailed")) {
            val jailSection = magenta.jailConfig.getJail().getConfigurationSection("jails") ?: return
            val randomJail = jailSection.getKeys(false).random()

            magenta.schedulerMagenta.runTask(magenta) {
                player.teleport(
                    Location(
                        Bukkit.getWorld(jailSection.getString("${randomJail}.location.world").toString()),
                        jailSection.getDouble("${randomJail}.location.x"),
                        jailSection.getDouble("${randomJail}.location.y"),
                        jailSection.getDouble("${randomJail}.location.z"),
                        jailSection.getInt("${randomJail}.location.yaw").toFloat(),
                        jailSection.getInt("${randomJail}.location.pitch").toFloat()
                    ))
            }
            event.isCancelled = true
        } else {
            event.isCancelled = false
        }
    }

}