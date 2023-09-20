package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.home.HomeDeleteEvent
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class HomeDeleteListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onHomeDelete(event: HomeDeleteEvent) {
        val player: Player = event.player
        val homeName: String = event.homeName

        val worlds = magenta.config.getStringList("warp.whitelist").contains(player.location.world.name)

        if (!worlds) {
            player.sendMessage(
                ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.home.error.blocked"),
                    TagResolver.resolver(Placeholder.parsed("world", player.location.world.name))))
            return
        }

        if (!magenta.homeModel.getHomeExist(homeName)) {
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.home.error.not.exist"),
                TagResolver.resolver(Placeholder.parsed("home", homeName))))
            return
        }

        magenta.homeModel.getHomes().filter { s -> s.homeName == homeName && s.owner != player.uniqueId.toString() }.find {
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("command-lynx-owner-delete-error"),
                TagResolver.resolver(
                    Placeholder.parsed("home", it.homeName),
                    ))
            )
            return
        }

        magenta.homeModel.deleteHome(homeName)
        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.home.success.deleted"),
            TagResolver.resolver(Placeholder.parsed("home", homeName))))
    }

}