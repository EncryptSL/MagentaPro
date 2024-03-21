package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.jail.JailCreateEvent
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class JailCreateListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onCreateJail(event: JailCreateEvent) {
        val player = event.player
        val jailName = event.jailName
        val location = event.location

        if (magenta.jailConfig.getConfig().getConfigurationSection(jailName) != null)
            return player.sendMessage(magenta.localeConfig.translation("magenta.command.jail.error.exist",
                Placeholder.parsed("jail", jailName)
            ))

        magenta.jailConfig.getConfig().set("jails.$jailName.location.world", location.world.name)
        magenta.jailConfig.getConfig().set("jails.$jailName.location.x", location.x)
        magenta.jailConfig.getConfig().set("jails.$jailName.location.y", location.y)
        magenta.jailConfig.getConfig().set("jails.$jailName.location.z", location.z)
        magenta.jailConfig.getConfig().set("jails.$jailName.location.yaw", location.yaw)
        magenta.jailConfig.getConfig().set("jails.$jailName.location.pitch", location.pitch)
        magenta.jailConfig.save()
        magenta.jailConfig.reload()

        return player.sendMessage(magenta.localeConfig.translation("magenta.command.jail.success.created", Placeholder.parsed("jail", jailName)))
    }

}