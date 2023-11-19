package com.github.encryptsl.magenta.api.chat

import org.bukkit.entity.Player

interface Chat {
    fun isDetected(player: Player, phrase: String): Boolean
}