package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.jail.JailPardonEvent
import com.github.encryptsl.magenta.api.events.jail.JailTeleportEvent
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class JailPardonListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onJailRelease(event: JailPardonEvent) {
        val target = event.player
        val user = magenta.user.getUser(target.uniqueId)

        val player = Bukkit.getPlayer(target.uniqueId)

        if (!user.isJailed() && !user.hasPunish()) return

        player?.sendMessage(magenta.localeConfig.translation("magenta.command.jail.success.unjailed"))

        player?.let { magenta.pluginManager.callEvent(JailTeleportEvent(player, user.getLastLocation())) }

        Bukkit.broadcast(magenta.localeConfig.translation("magenta.command.jail.success.unjailed.to",
            Placeholder.parsed("player", target.name.toString())
        ), "magenta.jail.pardon.event")

        user.resetDelay("jail")
        user.resetDelay("onlinejail")
    }

}