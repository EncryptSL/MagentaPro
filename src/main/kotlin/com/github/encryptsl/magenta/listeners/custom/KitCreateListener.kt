package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.kit.KitCreateEvent
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class KitCreateListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onCreateKit(event: KitCreateEvent) {
        val player = event.player
        val kitName = event.kitName
        val kitDelay = event.delay
        val kitManager = event.kitManager

        runCatching {
            kitManager.createKit(player, kitName, kitDelay)
        }.onSuccess {
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.kit.success.created"), TagResolver.resolver(
                Placeholder.parsed("kit", kitName),
                Placeholder.parsed("delay", kitDelay.toString())
            )))
        }.onFailure { e ->
            player.sendMessage(ModernText.miniModernText(e.message ?: e.localizedMessage))
        }
    }


}