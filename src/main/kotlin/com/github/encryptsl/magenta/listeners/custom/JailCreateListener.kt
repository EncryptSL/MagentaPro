package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.jail.JailCreateEvent
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class JailCreateListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onCreateJail(event: JailCreateEvent) {
        val player = event.player
        val jailName = event.jailName
        val location = event.location

        if (magenta.jailConfig.getJail().getConfigurationSection(jailName) != null)
            return player.sendMessage(
                ModernText.miniModernText(
                    magenta.localeConfig.getMessage("magenta.command.jail.error.exist"), TagResolver.resolver(
                        Placeholder.parsed("jail", jailName)
                    )
                )
            )

        magenta.jailConfig.getJail().set("jails.$jailName.location.x", location.x)
        magenta.jailConfig.getJail().set("jails.$jailName.location.y", location.y)
        magenta.jailConfig.getJail().set("jails.$jailName.location.z", location.z)
        magenta.jailConfig.getJail().set("jails.$jailName.location.yaw", location.yaw)
        magenta.jailConfig.getJail().set("jails.$jailName.location.pitch", location.pitch)
        magenta.jailConfig.save()
        magenta.jailConfig.reload()

        return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.jail.success.created"), TagResolver.resolver(
            Placeholder.parsed("jail", jailName)
        )))
    }

}