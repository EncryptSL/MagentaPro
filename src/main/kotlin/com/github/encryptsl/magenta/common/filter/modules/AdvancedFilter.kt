package com.github.encryptsl.magenta.common.filter.modules

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.chat.AbstractChatFilter
import com.github.encryptsl.magenta.api.chat.enums.Violations
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer

class AdvancedFilter(private val magenta: Magenta, private val violations: Violations) : AbstractChatFilter(magenta, violations) {

    override fun detection(event: AsyncChatEvent) {
        val player = event.player
        val message = PlainTextComponentSerializer.plainText().serialize(event.message())

        if (!magenta.config.getBoolean("chat.filters.${violations.name}.control")) return

        if (player.hasPermission("magenta.chat.filter.bypass.advanced.filter") || player.hasPermission("magenta.chat.filter.bypass.*"))
            return

        message.split(" ").forEach { messages ->
            if (messages.matches(Regex(magenta.config.getConfigurationSection("chat.filters.${violations.name}")?.getString("ip_regex").toString()))) {
                punishAction().punish(player, event, magenta.localeConfig.getMessage("magenta.filter.ip_filter"), null, null)
                event.isCancelled = true
                return
            }
            magenta.config.getConfigurationSection("chat.filters.${violations.name}")?.getStringList("web_regex")?.forEach {
                if (messages.matches(Regex(it))) {
                    punishAction().punish(player, event, magenta.localeConfig.getMessage("magenta.filter.web_filter"), null, null)
                    event.isCancelled = true
                    return
                }
            }
        }

    }
}