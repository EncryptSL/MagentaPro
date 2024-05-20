package com.github.encryptsl.magenta.common

import com.github.encryptsl.kmono.lib.extensions.playSound
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

object PlayerBuilderAction {

    private lateinit var player: Player

    fun player(player: Player): PlayerBuilderAction {
        this.player = player
        return this
    }

    fun sound(sound: String, volume: Float, pitch: Float): PlayerBuilderAction {
        playSound(this.player, sound, volume, pitch)
        return this
    }

    fun message(component: Component): PlayerBuilderAction {
        this.player.sendMessage(component)
        return this
    }
}