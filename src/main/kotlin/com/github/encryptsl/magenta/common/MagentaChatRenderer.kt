package com.github.encryptsl.magenta.common

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.kmono.lib.extensions.colorize
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.PluginPlaceholders
import com.github.encryptsl.magenta.common.hook.luckperms.LuckPermsAPI
import io.papermc.paper.chat.ChatRenderer
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.entity.Player
import java.util.Optional

class MagentaChatRenderer(
    private val magenta: Magenta,
    private val luckPermsHook: LuckPermsAPI,
    private val pluginPlaceholders: PluginPlaceholders
) : ChatRenderer {

    override fun render(
        source: Player,
        sourceDisplayName: Component,
        message: Component,
        viewer: Audience
    ): Component {
        val format = magenta.config.getString("chat.group-formats.${luckPermsHook.getGroup(source)}") ?: magenta.config.getString("chat.default-format").toString()

        val itemInHand = if (source.inventory.itemInMainHand.isEmpty) null else source.inventory.itemInMainHand.displayName()

        return ModernText.miniModernText(source, format, resolverTags(source,
            message.replaceText { replacement -> replacement.match("@everyone")
                .replacement(ModernText.miniModernText(magenta.config.getString("mentions.formats.everyone").toString())).build()
            }.replaceText { replacement ->
                replacement.matchLiteral("[item]").replacement(itemInHand).build()
            })
        )
    }

    private fun resolverTags(source: Player, text: Component): TagResolver {
        return TagResolver.resolver(
            Placeholder.parsed("world", source.world.name),
            Placeholder.component("name", Component.text(source.name)
                .clickEvent(ModernText.action(
                    ClickEvent.Action.SUGGEST_COMMAND,
                    Optional.ofNullable(
                        pluginPlaceholders.i8ln(source, magenta.config.getString("chat.suggestCommand").toString())
                    ).orElse("/tell ${source.name}"))
                )
            ),
            Placeholder.component("prefix", Component.text(colorize(luckPermsHook.getPrefix(source))).hoverEvent(hoverText(source))),
            Placeholder.parsed("suffix", colorize(luckPermsHook.getSuffix(source))),
            Placeholder.parsed("username_color", colorize(luckPermsHook.getMetaValue(source, "username-color"))),
            Placeholder.parsed("message_color", colorize(luckPermsHook.getMetaValue(source, "message-color"))),
            Placeholder.component("message", text)
        )
    }

    private fun hoverText(player: Player): HoverEvent<Component> {
        return ModernText.hover(
            HoverEvent.Action.SHOW_TEXT,
            Component.text(colorize(
                ModernText.papi(player,"\n${magenta.config.getString("chat.hoverText")}"))
            ))
    }
}