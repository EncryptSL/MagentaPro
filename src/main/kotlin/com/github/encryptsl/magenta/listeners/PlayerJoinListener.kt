package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.PlayerAccount
import com.github.encryptsl.magenta.api.events.jail.JailCheckEvent
import com.github.encryptsl.magenta.common.extensions.datetime
import com.github.encryptsl.magenta.common.extensions.parseMinecraftTime
import com.github.encryptsl.magenta.common.utils.FileUtil
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        val playerAccount = PlayerAccount(magenta, player.uniqueId)
        val newbies = magenta.config.getConfigurationSection("newbies")!!
        val kit = newbies.getString("kit") ?: "tools"
        magenta.teamIntegration.setTeam(player)

        val jailCheckEvent = JailCheckEvent(player)
        magenta.pluginManager.callEvent(jailCheckEvent)
        if(!jailCheckEvent.isCancelled) {
            magenta.logger.info("Hráč ${player.name} nemá trest ve vězení !")
        }

        if (magenta.config.getString("custom-join-message") != "none") {
            event.joinMessage(ModernText.miniModernText(
                magenta.config.getString("custom-join-message").toString(), TagResolver.resolver(
                    Placeholder.component("player", player.displayName())
                )
            ))
        }

        if (playerAccount.getAccount().contains("displayname")) {
            player.displayName(ModernText.miniModernText(playerAccount.getAccount().getString("displayname").toString()))
            player.playerListName(ModernText.miniModernText(playerAccount.getAccount().getString("displayname").toString()))
        }

        if (player.hasPlayedBefore()) {
            playerAccount.getAccount().set("timestamps.login", System.currentTimeMillis())
            playerAccount.save()

            FileUtil.getReadableFile(magenta.dataFolder, "motd.txt").forEach { text ->
                player.sendMessage(ModernText.miniModernText(text, TagResolver.resolver(
                    Placeholder.component("player", player.displayName()),
                    Placeholder.parsed("online", Bukkit.getOnlinePlayers().size.toString()),
                    Placeholder.parsed("worldtime", player.world.time.parseMinecraftTime()),
                    Placeholder.parsed("realtime", datetime())
                )))
            }

            return
        }
        if (kit.isNotEmpty()) {
            magenta.kitManager.giveKit(player, kit)
        }
        Bukkit.broadcast(ModernText.miniModernText(magenta.config.getString("newbies.announcement").toString(), TagResolver.resolver(
            Placeholder.parsed("player", player.name)
        )))
        playerAccount.getAccount().set("teleportenabled", true)
        playerAccount.getAccount().set("godmode", false)
        playerAccount.getAccount().set("jailed", false)
        playerAccount.getAccount().set("ip-address", player.address.address.hostAddress)
        playerAccount.getAccount().set("socialspy", false)
        playerAccount.getAccount().set("timestamps.lastteleport", 0)
        playerAccount.getAccount().set("timestamps.lastheal", 0)
        playerAccount.getAccount().set("timestamps.jail", 0)
        playerAccount.getAccount().set("timestamps.onlinejail", 0)
        playerAccount.getAccount().set("timestamps.logout", 0)
        playerAccount.getAccount().set("timestamps.login", System.currentTimeMillis())
        playerAccount.getAccount().set("lastlocation.world-name", player.world.name)
        playerAccount.getAccount().set("lastlocation.x", player.location.x)
        playerAccount.getAccount().set("lastlocation.y", player.location.y)
        playerAccount.getAccount().set("lastlocation.z", player.location.z)
        playerAccount.getAccount().set("lastlocation.yaw", player.location.yaw)
        playerAccount.getAccount().set("lastlocation.pitch", player.location.pitch)
        playerAccount.save()
    }

}