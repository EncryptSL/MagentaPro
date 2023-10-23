package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PlayerQuitListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        val user = magenta.user.getUser(player.uniqueId)

        if (magenta.config.getString("custom-quit-message") != "none") {
            event.quitMessage(ModernText.miniModernText(
                    magenta.config.getString("custom-quit-message").toString(), TagResolver.resolver(
                        Placeholder.component("player", player.displayName())
                    )
            ))
        }

        magenta.afk.clear(player.uniqueId)
        user.saveQuitData(player)
        user.saveLastLocation(player)
    }

}