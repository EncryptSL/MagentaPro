package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.jail.JailDeleteEvent
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class JailDeleteListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onDeleteJail(event: JailDeleteEvent) {
        val commandSender = event.commandSender
        val jailName = event.jailName

        runCatching {
             magenta.jailConfig.getJail().getConfigurationSection("jails")
        }.onSuccess {
            it?.set(jailName, null)
            magenta.jailConfig.save()
            magenta.jailConfig.reload()
            commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.jail.success.deleted"), TagResolver.resolver(
                Placeholder.parsed("jail", jailName)
            )))

        }.onFailure {
            commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.jail.error.not.exist"), TagResolver.resolver(
                Placeholder.parsed("jail", jailName)
            )))
        }
    }

}