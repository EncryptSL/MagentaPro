package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.kmono.lib.extensions.colorize
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.hook.luckperms.LuckPermsAPI
import com.github.encryptsl.magenta.common.model.MentionManager
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
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
        val message = ModernText.convertComponentToText(event.message())
        mentionManager.mentionProcess(event)

        hoverItem(player, message, event)

        if (user.isJailed()) {
            player.sendMessage(magenta.locale.translation("magenta.command.jail.error.event", Placeholder.parsed("action", "psát")))
            event.isCancelled = true
        }


        val it = recipients.iterator()
        while (it.hasNext()) {
            val u = magenta.user.getUser(it.next().uniqueId)
            if (u.isPlayerIgnored(player.uniqueId)) {
                it.remove()
            }
        }

        val suggestCommand = magenta.config.getString("chat.suggestCommand", "/tell $player ").toString()

        renderer(event, suggestCommand)
    }

    private fun renderer(event: AsyncChatEvent, suggestCommand: String) {
        try {
            val format = magenta.config.getString("chat.group-formats.${luckPermsHook.getGroup(event.player)}") ?: magenta.config.getString("chat.default-format").toString()
            event.renderer { source, _, ms, _ ->
                val plainText = ModernText.convertComponentToText(ms)
                ModernText.miniModernText(format,
                    TagResolver.resolver(
                        Placeholder.parsed("world", source.world.name),
                        Placeholder.component("name", Component.text(source.name)
                            .clickEvent(ModernText.action(
                                ClickEvent.Action.SUGGEST_COMMAND,
                                suggestCommand.replace("{name}", source.name).replace("{player}", source.name))
                            )
                        ),
                        Placeholder.component("prefix", Component.text(colorize(luckPermsHook.getPrefix(source))).hoverEvent(hoverText(source))),
                        Placeholder.parsed("suffix", colorize(luckPermsHook.getSuffix(source))),
                        Placeholder.parsed("username_color", colorize(luckPermsHook.getMetaValue(source, "username-color"))),
                        Placeholder.parsed("message_color", colorize(luckPermsHook.getMetaValue(source, "message-color"))),
                        Placeholder.parsed("message", if (source.hasPermission("magenta.chat.colors")) colorize(plainText) else plainText)
                    )
                )
            }
        } catch (_ : Exception) {}
    }

    private fun hoverText(player: Player): HoverEvent<Component> {
        return ModernText.hover(
            HoverEvent.Action.SHOW_TEXT,
            Component.text(colorize(
                ModernText.papi(player,"\n${magenta.config.getString("chat.hoverText")}"))
            ))
    }

    private fun hoverItem(player: Player, message: String, event: AsyncChatEvent) {
        val split = message.split(" ")
        for (m in split) {
            if (!m.equals("[item]", true)) continue

            if (player.inventory.itemInMainHand.isEmpty) return
            val itemStack = player.inventory.itemInMainHand

            event.message(ModernText.miniModernText(message.replace(m, m.replace("[item]", "<item>")), TagResolver.resolver(
                Placeholder.component("item", itemStack.displayName())
            )))
        }
    }
}