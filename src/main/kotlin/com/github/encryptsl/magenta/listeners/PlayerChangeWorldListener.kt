package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent

class PlayerChangeWorldListener(private val magenta: Magenta) : Listener {
    @EventHandler(priority = EventPriority.LOW)
    fun onPlayerChangeWorld(event: PlayerChangedWorldEvent) {
        val player = event.player

        if (!magenta.config.getBoolean("change-world-message")) return

        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.player.change.world"), TagResolver.resolver(
            Placeholder.parsed("world", player.world.name)
        )))
    }
}