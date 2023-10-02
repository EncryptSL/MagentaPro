package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.account.PlayerAccount
import com.github.encryptsl.magenta.api.events.jail.JailPlayerEvent
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class JailPlayerListener(private val magenta: Magenta) : Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onJailPlayer(event: JailPlayerEvent) {
        val player = event.player
        val action = event.action
        val account = PlayerAccount(magenta, player.uniqueId)

        if (account.jailManager.hasPunish() || account.isJailed()) {
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.jail.error.event"), TagResolver.resolver(
                Placeholder.parsed("action", action)
            )))
            event.isCancelled = true
        } else {
            event.isCancelled = false
        }
    }

}