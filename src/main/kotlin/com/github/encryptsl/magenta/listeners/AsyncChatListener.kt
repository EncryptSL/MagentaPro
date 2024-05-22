package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.PluginPlaceholders
import com.github.encryptsl.magenta.common.MagentaChatRenderer
import com.github.encryptsl.magenta.common.hook.luckperms.LuckPermsAPI
import com.github.encryptsl.magenta.common.model.MentionManager
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class AsyncChatListener(private val magenta: Magenta) : Listener {

    private val pluginPlaceholders: PluginPlaceholders by lazy { PluginPlaceholders(magenta) }
    private val luckPermsHook: LuckPermsAPI by lazy { LuckPermsAPI() }
    private val renderer: MagentaChatRenderer by lazy {
        MagentaChatRenderer(
            magenta,
            luckPermsHook,
            pluginPlaceholders
        )
    }
    private val mentionManager: MentionManager by lazy { MentionManager(magenta) }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun chat(event: AsyncChatEvent) {
        val player = event.player
        val recipients = HashSet(Bukkit.getOnlinePlayers())
        val user = magenta.user.getUser(player.uniqueId)

        if (user.isJailed()) {
            player.sendMessage(
                magenta.locale.translation(
                    "magenta.command.jail.error.event",
                    Placeholder.parsed("action", "psát")
                )
            )
            event.isCancelled = true
        }

        val it = recipients.iterator()
        while (it.hasNext()) {
            val u = magenta.user.getUser(it.next().uniqueId)
            if (u.isPlayerIgnored(player.uniqueId)) {
                it.remove()
            }
        }

        event.renderer(renderer)
        mentionManager.mentionProcess(event)
    }
}