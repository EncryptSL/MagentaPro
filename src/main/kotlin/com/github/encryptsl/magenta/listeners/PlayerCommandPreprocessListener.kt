package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.PlayerAccount
import com.github.encryptsl.magenta.common.utils.ModernText
import io.papermc.paper.event.player.PlayerSignCommandPreprocessEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

class PlayerCommandPreprocessListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onPlayerCommandPreprocess(event: PlayerCommandPreprocessEvent) {
        val player = event.player
        val message = event.message
        val command = message.split("")[0].replace("/", "").lowercase()
        val list = magenta.config.getStringList("socialspy-commands")
        if (list.contains(command) || list.contains("*")) {
            if (command != "msg" && command != "r") {
                if (!player.hasPermission("magenta.chat.spy.exempt")) {
                    Bukkit.getServer().onlinePlayers
                        .filter { PlayerAccount(magenta, it.uniqueId).getAccount().getBoolean("socialspy") }
                        .forEach { p -> if (p.name == p.name) return p.sendMessage(ModernText.miniModernText("[SOCIALSPY] ${player.name} : $message")) }
                }
            }
        }
    }
}