package com.github.encryptsl.magenta.common

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import org.bukkit.Sound
import org.bukkit.entity.Player

object PlayerBuilderAction {

    private lateinit var player: Player

    fun player(player: Player): PlayerBuilderAction {
        this.player = player
        return this
    }

    private fun audience(): Audience {
        return Audience.audience(this.player)
    }

    fun sound(sound: Sound, volume: Float, pitch: Float): PlayerBuilderAction {
        audience().playSound(net.kyori.adventure.sound.Sound.sound().volume(volume).pitch(pitch).type(Key.key(sound.name)).build())
        return this
    }

    fun message(component: Component): PlayerBuilderAction {
        audience().sendMessage(component)
        return this
    }

}