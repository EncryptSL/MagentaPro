package com.github.encryptsl.magenta.common.filter

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.chat.enums.Violations
import com.github.encryptsl.magenta.common.utils.ModernText
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class ChatPunishManager(private val magenta: Magenta, private val violations: Violations) {


    fun punish(player: Player, event: AsyncChatEvent, message: String, replace: String?, replacement: String?) {

        val chatMessage = PlainTextComponentSerializer.plainText().serialize(event.message())

        magenta.config.getConfigurationSection("chat.filters.${violations.name.lowercase()}")?.getStringList("action")?.forEach {
            if (it.equals("none", false)) return

            if (it.equals("notify", false)) {
                Bukkit.broadcast(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.filter.admin.notify"), TagResolver.resolver(
                    Placeholder.parsed("player", player.name), Placeholder.parsed("message", chatMessage))), "magenta.admin.notify")
            } else if (it.equals("kick", false)) {
                player.kick(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.filter.action.kick"), TagResolver.resolver(
                    Placeholder.parsed("reason", violations.name))
                ))
            } else if (it.equals("message", false)) {
                player.sendMessage(Component.text(message))
            } else if (it.equals("replace", false)) {
                event.renderer { _, _, message, _ -> message.replaceText(TextReplacementConfig.builder().match("(.*) + $replace + (.*)").replacement(replacement.toString()).build()) }
            }
        }
    }
}