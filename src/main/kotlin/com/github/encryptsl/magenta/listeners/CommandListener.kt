package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.Permissions
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

class CommandListener(private val magenta: Magenta) : Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerCommandPreprocess(
        event: PlayerCommandPreprocessEvent
    ) {
        val player = event.player
        val message = event.message
        val command = message.split(" ")[0].replace("/", "").lowercase()

        if (player.hasPermission(Permissions.SOCIAL_SPY_EXEMPT)) return

        val isListed = magenta.stringUtils.inInList("socialspy-commands", command) || magenta.stringUtils.inInList("socialspy-commands", "*")
        if (!isListed) return

        for (p in Bukkit.getOnlinePlayers()) {
            if (!magenta.user.getUser(p.uniqueId).isSocialSpy() && !player.hasPermission(Permissions.SOCIAL_SPY)) continue
            if (!p.canSee(player)) continue

            p.sendMessage(ModernText.miniModernText(
                magenta.config.getString("socialspy-format").toString(), TagResolver.resolver(
                    Placeholder.parsed("player", player.name), Placeholder.parsed("message", message)
                )
            ))
        }
    }
}