package com.github.encryptsl.magenta.common.filter

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.chat.enums.Violations
import com.github.encryptsl.magenta.common.utils.ModernText
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class ChatPunishManager(private val magenta: Magenta, private val violations: Violations) {

    fun action(player: Player, event: AsyncChatEvent, translation: Component?, replace: TextReplacementConfig?, messageFromChat: String) {
        val actionList = magenta.config.getStringList("chat.filters.${violations.name.lowercase()}.action")
        if (actionList.contains("none")) return

        if (actionList.contains("kick")) {
            magenta.schedulerMagenta.doSync(magenta) {
                player.kick(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.filter.action.kick"), TagResolver.resolver(
                    Placeholder.parsed("reason", violations.name))
                ))
            }
        }

        if (actionList.contains("notify")) {
            Bukkit.broadcast(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.filter.admin.notify"), TagResolver.resolver(
                Placeholder.parsed("player", player.name), Placeholder.parsed("message", messageFromChat))))
        }

        if (actionList.contains("message")) {
            if (translation != null) {
                player.sendMessage(translation)
            }
        }
        if (actionList.contains("replace")) {
            replace?.let {
                event.message(event.message().replaceText(replace))
            }
        }
        if (actionList.contains("cancel")) {
            event.isCancelled = true
        }
    }
}