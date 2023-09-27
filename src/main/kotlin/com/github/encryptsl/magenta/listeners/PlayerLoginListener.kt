package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.PlayerAccount
import com.github.encryptsl.magenta.api.events.jail.JailCheckEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent

class PlayerLoginListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onLogin(event: PlayerLoginEvent) {
        val player = event.player
        val playerAccount = PlayerAccount(magenta, player.uniqueId)
        val account = playerAccount.getAccount()
        val newbies = magenta.config.getConfigurationSection("newbies")!!
        val kit = newbies.getString("kit") ?: "hrac"
        magenta.teamIntegration.setTeam(player)

        val jailCheckEvent = JailCheckEvent(player)
        magenta.pluginManager.callEvent(jailCheckEvent)
        if(jailCheckEvent.isCancelled) {
            magenta.logger.info("Hráč ${player.name} nemá trest ve vězení !")
        }

        if (player.hasPlayedBefore()) {
            account.set("timestamps.login", System.currentTimeMillis())
            playerAccount.save()
            playerAccount.reload()
            return
        }

        if (kit.isNotEmpty()) {
            magenta.kitManager.giveKit(player, kit)
        }

        account.set("teleportenabled", true)
        account.set("godmode", false)
        account.set("jailed", false)
        account.set("ip-address", event.address.hostAddress)
        account.set("socialspy", false)
        account.set("timestamps.lastteleport", 0)
        account.set("timestamps.lastheal", 0)
        account.set("timestamps.jail", 0)
        account.set("timestamps.onlinejail", 0)
        account.set("timestamps.logout", 0)
        account.set("timestamps.login", System.currentTimeMillis())
        account.set("lastlocation.world-name", player.world.name)
        account.set("lastlocation.x", player.location.x)
        account.set("lastlocation.y", player.location.y)
        account.set("lastlocation.z", player.location.z)
        account.set("lastlocation.yaw", player.location.yaw)
        account.set("lastlocation.pitch", player.location.pitch)
        playerAccount.save()
        playerAccount.reload()
    }

}