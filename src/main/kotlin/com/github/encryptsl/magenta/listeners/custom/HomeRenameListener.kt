package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.home.HomeRenameEvent
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class HomeRenameListener(private val magenta: Magenta) : Listener {
    @EventHandler
    fun onHomeRename(event: HomeRenameEvent) {
        val player: Player = event.player
        val location: Location = player.location
        val oldHomeName: String = event.fromHomeName
        val newHomeName: String = event.toHomeName

        val worlds = magenta.config.getStringList("warp.whitelist").contains(player.location.world.name)

        if (!worlds)
            return player.sendMessage(
                ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.home.error.blocked"),
                    TagResolver.resolver(Placeholder.parsed("world", location.world.name))))

        if (!magenta.homeModel.getHomeExist(player, oldHomeName))
            return player.sendMessage(
                ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.home.error.not.exist"),
                    TagResolver.resolver(Placeholder.parsed("home", oldHomeName))))

        magenta.homeModel.renameHome(player, oldHomeName, newHomeName)
        player.sendMessage(
            ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.home.success.renamed"),
                TagResolver.resolver(
                    Placeholder.parsed("new_home", newHomeName),
                    Placeholder.parsed("old_home", oldHomeName)
                )
            )
        )
    }
}