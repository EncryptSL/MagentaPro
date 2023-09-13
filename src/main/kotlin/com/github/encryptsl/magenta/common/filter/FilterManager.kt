package com.github.encryptsl.magenta.common.filter

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.chat.Violations
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class FilterManager(private val magenta: Magenta, val violations: Violations) {


    fun action(player: Player, event: AsyncChatEvent, message: String, replace: String?, replacement: String?) {

        val chatMessage = PlainTextComponentSerializer.plainText().serialize(event.message())

        magenta.config.getConfigurationSection("chat.filters.${violations.name.lowercase()}")?.getStringList("action")?.forEach {
            if (it.equals("none", false)) return

            if (it.equals("notify", false)) {
                Bukkit.broadcast(Component.text(magenta.locale.getProperty("magenta.filter.admin.notify").toString().format(player.name, chatMessage)), "magenta.admin.notify")
            } else if (it.equals("kick", false)) {
                player.kick(Component.text(magenta.locale.getProperty("magenta.filter.action.kick").toString().format(violations.name)))
            } else if (it.equals("message", false)) {
                player.sendMessage(Component.text(message))
            } else if (it.equals("replace", false)) {
                event.renderer { _, _, message, _ -> message.replaceText(TextReplacementConfig.builder().match("(.*) + $replace + (.*)").replacement(replacement.toString()).build()) }
            }
        }
    }
}