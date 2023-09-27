package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.kit.KitAdminGiveEvent
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class KitAdminGiveListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onKitAdminGive(event: KitAdminGiveEvent) {
        val commandSender = event.commandSender
        val target = event.target
        val kitName = event.kitName
        val kitManager = event.kitManager

        runCatching {
            kitManager.giveKit(target, kitName)
        }.onSuccess {
            target.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.kit.success.given.self"), TagResolver.resolver(
                Placeholder.parsed("kit", kitName)
            )))
            commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.kit.success.given.to"), TagResolver.resolver(
                Placeholder.parsed("username", target.name),
                Placeholder.parsed("kit", kitName)
            )))
        }.onFailure { e ->
            commandSender.sendMessage(e.message ?: e.localizedMessage)
            commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.kit.error.not.exist")))
        }
    }

}