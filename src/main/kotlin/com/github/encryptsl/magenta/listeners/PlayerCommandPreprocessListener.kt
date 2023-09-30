package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.PlayerAccount
import io.papermc.paper.event.player.PlayerSignCommandPreprocessEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

class PlayerCommandPreprocessListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onPlayerCommandPreprocess(event: PlayerCommandPreprocessEvent) {
        val player = event.player
        val account = PlayerAccount(magenta, player.uniqueId)
        val command = event.message.split("")[0].replace("/", "").lowercase()
        val list = magenta.config.getStringList("socialspy-commands")
        if (list.contains(command) || list.contains("*")) {
            if (command != "msg" && command != "r") {
                if (!player.hasPermission("magenta.chat.spy.exempt")) {

                }
            }
        }

    }


}