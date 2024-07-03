package com.github.encryptsl.magenta.common

import com.github.benmanes.caffeine.cache.*
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.filter.impl.ChatFilterAntiSpamListener
import com.github.encryptsl.magenta.common.model.TpaManager
import com.google.common.cache.CacheBuilder
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.time.Duration
import java.util.*

class PlayerCacheManager(private val magenta: Magenta) {
    val reply: Cache<Player, Player> = Caffeine.newBuilder().expireAfterWrite(Duration.ofMinutes(5)).build()
    val antiSpam: com.google.common.cache.Cache<UUID, String> = CacheBuilder.newBuilder().expireAfterWrite(Duration.ofSeconds(60)).removalListener(
        ChatFilterAntiSpamListener<UUID, String>()
    ).build()
    val teleportRequest: Cache<UUID, TpaManager.TpaRequest> = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofSeconds(magenta.config.getLong("tpa-accept-cancellation")))
        .scheduler(Scheduler.systemScheduler())
        .removalListener(RemovalListener<UUID, TpaManager.TpaRequest> { key, value, cause ->
            if (cause == RemovalCause.EXPIRED) {
                val target = key?.let { Bukkit.getPlayer(it) }
                val sender = value?.from?.let { Bukkit.getPlayer(it) }

                target?.sendMessage(magenta.locale.translation("magenta.command.tpa.error.request.expired"))
                sender?.let {
                    PlayerBuilderAction
                        .player(sender)
                        .message(magenta.locale.translation("magenta.command.tpa.error.request.expired.to",
                            Placeholder.parsed("player", Bukkit.getOfflinePlayer(value.to).name.toString())
                        )).sound("block.note_block.bass", 1.5F, 1.5F)
                }
            }
        }).build()
}