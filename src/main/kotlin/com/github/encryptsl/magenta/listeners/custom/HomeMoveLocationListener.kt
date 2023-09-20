package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.home.HomeMoveLocationEvent
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class HomeMoveLocationListener(private val magenta: Magenta) : Listener {
    @EventHandler
    fun onHomeInfo(event: HomeMoveLocationEvent) {
        val player: Player = event.player
        val location: Location = player.location
        val homeName: String = event.homeName

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
        magenta.homeModel.getHomes().filter { s -> s.homeName == homeName && s.owner != player.uniqueId.toString() }.find {
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("command-lynx-owner-modify-error"),
                TagResolver.resolver(
                    Placeholder.parsed("home", it.homeName),
                )
            ))
            return
        }

        magenta.homeModel.moveHome(homeName, location)
        player.sendMessage(
            ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.home.success.moved"),
                TagResolver.resolver(
                    Placeholder.parsed("home", homeName),
                    Placeholder.parsed("x", location.x.toInt().toString()),
                    Placeholder.parsed("y", location.y.toInt().toString()),
                    Placeholder.parsed("z", location.z.toInt().toString())
                )
            )
        )
    }
}