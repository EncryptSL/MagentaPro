package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.account.PlayerAccount
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

class PlayerCommandPreprocessListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onPlayerCommandPreprocess(event: PlayerCommandPreprocessEvent) {
        val player = event.player
        val message = event.message
        val command = message.split(" ")[0].replace("/", "").lowercase()
        val list = magenta.config.getStringList("socialspy-commands")
        if (list.contains(command) || list.contains("*")) {
            if (!player.hasPermission("magenta.social.spy.exempt")) {
                Bukkit.getServer().onlinePlayers
                    .filter { PlayerAccount(magenta, it.uniqueId).isSocialSpy() && it.hasPermission("magenta.social.spy") }
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