package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.home.HomeTeleportEvent
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class HomeTeleportListener(private val magenta: Magenta) : Listener {
    @EventHandler
    fun onHomeTeleport(event: HomeTeleportEvent) {
        val homeName = event.homeName
        val player: Player = event.player
        val location: Location = player.location

        val worlds = magenta.config.getStringList("warp.whitelist").contains(player.location.world.name)

        if (!worlds) {
            player.sendMessage(
                ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.home.error.blocked"),
                    TagResolver.resolver(Placeholder.parsed("world", location.world.name))))
            return
        }

        if (!magenta.homeModel.getHomeExist(homeName)) {
            player.sendMessage(
                ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.home.error.not.exist"),
                    TagResolver.resolver(Placeholder.parsed("home", homeName))))
            return
        }

        magenta.homeModel.getHomes().filter { s -> s.homeName == homeName }.first {
            player.teleport(Location(Bukkit.getWorld(it.world), it.x.toDouble(), it.y.toDouble(), it.z.toDouble(), it.yaw, it.pitch))
        }

        player.sendMessage(
            ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.home.success.teleport"),
                TagResolver.resolver(Placeholder.parsed("home", homeName))))
    }
}