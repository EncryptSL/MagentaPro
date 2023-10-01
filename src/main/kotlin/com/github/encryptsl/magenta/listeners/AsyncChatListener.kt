package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.JailManager
import com.github.encryptsl.magenta.api.MentionManager
import com.github.encryptsl.magenta.api.PlayerAccount
import com.github.encryptsl.magenta.api.events.chat.PlayerMentionEvent
import com.github.encryptsl.magenta.api.events.jail.JailPlayerEvent
import com.github.encryptsl.magenta.common.utils.ModernText
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener


class AsyncChatListener(private val magenta: Magenta) : Listener {

    private val mentionManager: MentionManager by lazy { MentionManager(magenta) }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun chat(event: AsyncChatEvent) {
        val player = event.player
        val account = PlayerAccount(magenta, player.uniqueId)

        mentionManager.mentionProcess(event)
        if (account.jailManager.hasPunish()) {
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.jail.error.event"), TagResolver.resolver(
                Placeholder.parsed("action", "psát")
            )))
            event.isCancelled = true
        }
    }
}