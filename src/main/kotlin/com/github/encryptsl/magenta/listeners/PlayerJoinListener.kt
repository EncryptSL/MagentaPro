package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.jail.JailCheckEvent
import com.github.encryptsl.magenta.common.extensions.datetime
import com.github.encryptsl.magenta.common.extensions.parseMinecraftTime
import com.github.encryptsl.magenta.common.utils.FileUtil
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        val user = magenta.user.getUser(player.uniqueId)
        val newbies = magenta.config.getConfigurationSection("newbies")!!
        val kit = newbies.getString("kit") ?: "tools"

        magenta.pluginManager.callEvent(JailCheckEvent(player))

        if (!magenta.config.getString("custom-join-message").equals("none", ignoreCase = true)) {
            event.joinMessage(ModernText.miniModernText(
                magenta.config.getString("custom-join-message").toString(), TagResolver.resolver(
                    Placeholder.component("player", player.displayName())
                )
            ))
        }

        safeFly(player)

        if (user.getAccount().contains("displayname")) {
            player.displayName(ModernText.miniModernText(user.getAccount().getString("displayname").toString()))
            player.playerListName(ModernText.miniModernText(user.getAccount().getString("displayname").toString()))
        }

        if (player.hasPlayedBefore()) {
            user.set("timestamps.login", System.currentTimeMillis())

            FileUtil.getReadableFile(magenta.dataFolder, "motd.txt").forEach { text ->
                player.sendMessage(ModernText.miniModernTextCenter(text, TagResolver.resolver(
                    Placeholder.component("player", player.displayName()),
                    Placeholder.parsed("online", Bukkit.getOnlinePlayers().size.toString()),
                    Placeholder.parsed("worldtime", player.world.time.parseMinecraftTime()),
                    Placeholder.parsed("realtime", datetime())
                )))
            }
            if (user.getAccount().contains("votifier.rewards")) {
                player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.vote.success.exist.rewards.to.claim")))
            }
            return
        }
        if (kit.isNotEmpty()) {
            magenta.kitManager.giveKit(player, kit)
        }
        Bukkit.broadcast(ModernText.miniModernText(magenta.config.getString("newbies.announcement").toString(), TagResolver.resolver(
            Placeholder.parsed("player", player.name),
            Placeholder.parsed("joined", Bukkit.getOfflinePlayers().size.toString())
        )))
        user.createDefaultData(player)
    }

    private fun safeFly(player: Player) {
        if (player.hasPermission("magenta.fly.safelogin")) {
            player.fallDistance = 0F
            player.allowFlight = true
            player.isFlying = true
        }
    }

}