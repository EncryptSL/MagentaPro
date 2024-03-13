package com.github.encryptsl.magenta.common

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import org.bukkit.entity.Player
import java.time.Duration

class PlayerCacheManager {
    val reply: Cache<Player, Player> = CacheBuilder.newBuilder().expireAfterWrite(Duration.ofMinutes(5)).build()
}