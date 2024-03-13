package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.hook.luckperms.LuckPermsAPI
import com.github.encryptsl.magenta.common.model.MentionManager
import com.github.encryptsl.magenta.common.utils.ModernText
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class AsyncChatListener(private val magenta: Magenta) : Listener {

    private val mentionManager: MentionManager by lazy { MentionManager(magenta) }
    private val luckPermsHook: LuckPermsAPI by lazy { LuckPermsAPI() }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun chat(event: AsyncChatEvent) {
        val player = event.player
        val recipients = HashSet(Bukkit.getOnlinePlayers())
        val user = magenta.user.getUser(player.uniqueId)
        val message = PlainTextComponentSerializer.plainText().serialize(event.message())
        mentionManager.mentionProcess(event)

        if (message.contains("[item]")) {
            hoverItem(player, message, event)
        }

        if (user.isJailed()) {
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.jail.error.event"), TagResolver.resolver(
                Placeholder.parsed("action", "psát")
            )))
            event.isCancelled = true
        }

        val format = magenta.config.getString("chat.group-formats.${luckPermsHook.getGroup(player)}") ?: magenta.config.getString("chat.default-format").toString()
        val suggestCommand = magenta.config.getString("chat.suggestCommand") ?: ""

        val it = recipients.iterator()
        while (it.hasNext()) {
            val u = magenta.user.getUser(it.next().uniqueId)
            if (u.isPlayerIgnored(player.uniqueId)) {
                it.remove()
            }
        }

        event.renderer { source, _, ms, _ ->
            val plainText = PlainTextComponentSerializer.plainText().serialize(ms)
            ModernText.miniModernText(format,
                TagResolver.resolver(
                    Placeholder.parsed("world", source.world.name),
                    Placeholder.component("name", Component.text(source.name)
                        .clickEvent(ModernText.action(
                            ClickEvent.Action.SUGGEST_COMMAND,
                            suggestCommand.replace("{name}", source.name).replace("{player}", source.name))
                        )
                    ),
                    Placeholder.component("prefix", Component.text(magenta.stringUtils.colorize(luckPermsHook.getPrefix(player))).hoverEvent(hoverText(source))),
                    Placeholder.parsed("suffix", magenta.stringUtils.colorize(luckPermsHook.getSuffix(player))),
                    Placeholder.parsed("username_color", magenta.stringUtils.colorize(luckPermsHook.getMetaValue(player, "username-color"))),
                    Placeholder.parsed("message_color", magenta.stringUtils.colorize(luckPermsHook.getMetaValue(player, "message-color"))),
                    Placeholder.parsed("message", if (player.hasPermission("magenta.chat.colors")) magenta.stringUtils.colorize(plainText) else plainText)
                )
            )
        }
    }
    private fun hoverText(player: Player): HoverEvent<Component> {
        return ModernText.hover(HoverEvent.Action.SHOW_TEXT, Component.text(magenta.stringUtils.colorize(ModernText.papi(player,"\n${magenta.config.getString("chat.hoverText")}"))))
    }

    private fun hoverItem(player: Player, message: String, event: AsyncChatEvent) {
        val split = message.split(" ")
        for (m in split) {
            if (m.equals("[item]", true)) {
                if (player.inventory.itemInMainHand.isEmpty) return
                val itemStack = player.inventory.itemInMainHand

                event.message(ModernText.miniModernText(message.replace(m, m.replace("[item]", "<item>")), TagResolver.resolver(
                    Placeholder.component("item", itemStack.displayName())
                )))
            }
        }
    }
}