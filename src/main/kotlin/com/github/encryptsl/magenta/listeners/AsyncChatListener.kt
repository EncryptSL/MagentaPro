package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.manager.MentionManager
import com.github.encryptsl.magenta.common.utils.ModernText
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener


class AsyncChatListener(private val magenta: Magenta) : Listener {

    private val mentionManager: MentionManager by lazy { MentionManager(magenta) }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun chat(event: AsyncChatEvent) {
        val player = event.player
        val user = magenta.user.getUser(player.uniqueId)
        val message = PlainTextComponentSerializer.plainText().serialize(event.message())
        mentionManager.mentionProcess(event)

        if (message.contains("[item]")) {
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

        if (user.jailManager.hasPunish()) {
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.jail.error.event"), TagResolver.resolver(
                Placeholder.parsed("action", "psát")
            )))
            event.isCancelled = true
        }
    }
}