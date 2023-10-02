package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.home.HomeCreateEvent
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class HomeCreateListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onHomeCreate(event: HomeCreateEvent) {
        val homeName = event.homeName
        val player: Player = event.player
        val location: Location = event.location

        val worlds = magenta.config.getStringList("warps.whitelist").contains(location.world.name)

        if (!worlds)
            return player.sendMessage(
                ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.home.error.blocked"),
                    TagResolver.resolver(Placeholder.parsed("world", location.world.name))))

        if (magenta.homeModel.getHomeExist(player, homeName))
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.home.error.exist"),
                TagResolver.resolver(Placeholder.parsed("home", homeName))))

        if (magenta.homeModel.canSetHome(player))
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.home.error.limit")))

        magenta.homeModel.createHome(player, location, homeName)
        player.sendMessage(
            ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.home.success.created"),
                TagResolver.resolver(Placeholder.parsed("home", homeName))))
    }

}