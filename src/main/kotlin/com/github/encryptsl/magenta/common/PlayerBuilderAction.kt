package com.github.encryptsl.magenta.common

import net.kyori.adventure.text.Component
import org.bukkit.Sound
import org.bukkit.entity.Player

object PlayerBuilderAction {

    private lateinit var player: Player

    fun player(player: Player): PlayerBuilderAction {
        this.player = player
        return this
    }

    fun sound(sound: Sound, volume: Float, pitch: Float): PlayerBuilderAction {
        this.player.playSound(player.location, sound, volume, pitch)
        return this
    }

    fun message(component: Component): PlayerBuilderAction {
        this.player.sendMessage(component)
        return this
    }

}