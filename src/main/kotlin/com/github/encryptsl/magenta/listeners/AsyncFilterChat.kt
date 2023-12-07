package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.chat.enums.Violations
import com.github.encryptsl.magenta.common.filter.ChatPunishManager
import com.github.encryptsl.magenta.common.filter.modules.*
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class AsyncFilterChat(private val magenta: Magenta) : Listener {

    private val chatPunishManager = ChatPunishManager(magenta)

    private val antiSpam: AntiSpam by lazy { AntiSpam(magenta) }
    private val capsLock: CapsLock by lazy { CapsLock(magenta) }
    private val ipFilter: IPFilter by lazy { IPFilter(magenta) }
    private val swear: Swear by lazy { Swear(magenta) }
    private val websiteFilter: WebsiteFilter by lazy { WebsiteFilter(magenta) }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onAsyncChat(event: AsyncChatEvent) {
        val player: Player = event.player
        val phrase = PlainTextComponentSerializer.plainText().serialize(event.message())
        if(antiSpam.isDetected(player, phrase)) {
            magenta.schedulerMagenta.delayedTask(magenta, {
                SpamCache.spam.remove(player.uniqueId, SpamCache.spam[player.uniqueId].toString())
            }, 1000L)
            chatPunishManager.action(player,
                event,
                magenta.localeConfig.getMessage("magenta.filter.antispam"),
                "Spamovat",
                Violations.ANTISPAM
            )
            return
        }
        if(capsLock.isDetected(player, phrase)) {
            chatPunishManager.action(
                player,
                event,
                magenta.localeConfig.getMessage("magenta.filter.caps"),
                "Psát CapsLockem",
                Violations.CAPSLOCK
            )
            return
        }
        if(ipFilter.isDetected(player, phrase)) {
            chatPunishManager.action(player, event, magenta.localeConfig.getMessage("magenta.filter.ip_filter"), phrase, Violations.IPFILTER)
        }
        if(swear.isDetected(player, phrase)) {
            chatPunishManager.action(
                player,
                event,
                magenta.localeConfig.getMessage("magenta.filter.swear"),
                phrase,
                Violations.SWEAR
            )
            return
        }
        if(websiteFilter.isDetected(player, phrase)) {
            chatPunishManager.action(player, event, magenta.localeConfig.getMessage("magenta.filter.web_filter"), phrase, Violations.WEBSITE)
            return
        }
    }

    private fun debug(player: Player, violations: Violations) {
        player.sendMessage("Is Detected : ${violations.name}")
    }

}