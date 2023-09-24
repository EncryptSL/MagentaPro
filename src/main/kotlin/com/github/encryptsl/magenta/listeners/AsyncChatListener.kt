package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.chat.enums.Violations
import com.github.encryptsl.magenta.api.events.jail.JailPlayerEvent
import com.github.encryptsl.magenta.common.filter.modules.AdvancedFilter
import com.github.encryptsl.magenta.common.filter.modules.AntiSpam
import com.github.encryptsl.magenta.common.filter.modules.CapsLock
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class AsyncChatListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun chat(event: AsyncChatEvent) {
        val player = event.player
        AntiSpam(magenta, Violations.SPAM).detection(event)
        CapsLock(magenta, Violations.CAPSLOCK).detection(event)
        AdvancedFilter(magenta, Violations.ADVANCED_FILTER).detection(event)

        val jailPlayerEvent = JailPlayerEvent(player, "psát !")
        if (jailPlayerEvent.isCancelled) {
            player.playSound(Sound.sound().type(Key.key("minecraft:block.note_block.bass")).source(Sound.Source.BLOCK).volume(1.5F).build())
            event.isCancelled = true
        }
    }
}