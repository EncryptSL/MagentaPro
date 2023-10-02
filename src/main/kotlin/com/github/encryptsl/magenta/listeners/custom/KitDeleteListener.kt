package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.kit.KitDeleteEvent
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class KitDeleteListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onKitDelete(event: KitDeleteEvent) {
        val commandSender = event.commandSender
        val kitName = event.kitName
        runCatching {
            magenta.kitManager.deleteKit(kitName)
        }.onSuccess {
            commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.kit.success.deleted"), Placeholder.parsed("kit", kitName)))
        }.onFailure { e ->
            commandSender.sendMessage(ModernText.miniModernText(e.message ?: e.localizedMessage))
        }
    }

}