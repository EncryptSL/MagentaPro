package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.jail.JailDeleteEvent
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class JailDeleteListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onDeleteJail(event: JailDeleteEvent) {
        val commandSender = event.commandSender
        val jailName = event.jailName

        try {
            val jails = magenta.jailConfig.getConfig().getConfigurationSection("jails")
            jails?.set(jailName, null)
            magenta.jailConfig.save()
            magenta.jailConfig.reload()
            commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.jail.success.deleted", Placeholder.parsed("jail", jailName)))
        } catch (e : Exception) {
            commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.jail.error.not.exist", Placeholder.parsed("jail", jailName)))
        }
    }

}