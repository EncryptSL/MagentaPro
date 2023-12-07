package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.account.UserAccount
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

class PlayerCommandPreprocessListener(private val magenta: Magenta) : Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerCommandPreprocess(event: PlayerCommandPreprocessEvent) {
        val player = event.player
        val message = event.message
        val command = message.split(" ")[0].replace("/", "").lowercase()
        if (magenta.stringUtils.inInList("socialspy-commands", command) || magenta.stringUtils.inInList("socialspy-commands", "*")) {
            if (!player.hasPermission("magenta.social.spy.exempt")) {
                Bukkit.getServer().onlinePlayers
                    .filter { UserAccount(magenta, it.uniqueId).isSocialSpy() && it.hasPermission("magenta.social.spy") }
                    .forEach { p ->
                        p.sendMessage(
                            ModernText.miniModernText(
                                magenta.config.getString("socialspy-format").toString(), TagResolver.resolver(
                                    Placeholder.parsed("player", player.name), Placeholder.parsed("message", message)
                                )
                            )
                        )
                    }
            }
        }
    }
}