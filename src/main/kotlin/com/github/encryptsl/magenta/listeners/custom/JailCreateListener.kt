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


        val jailSection = magenta.jailConfig.getConfig().getConfigurationSection("jails") ?: return

        jailSection.set("$jailName.location.world", location.world.name)
        jailSection.set("$jailName.location.x", location.x)
        jailSection.set("$jailName.location.y", location.y)
        jailSection.set("$jailName.location.z", location.z)
        jailSection.set("$jailName.location.yaw", location.yaw)
        jailSection.set("$jailName.location.pitch", location.yaw)
        magenta.jailConfig.save()
        magenta.jailConfig.reload()

        return player.sendMessage(magenta.localeConfig.translation("magenta.command.jail.success.created", Placeholder.parsed("jail", jailName)))
    }

}