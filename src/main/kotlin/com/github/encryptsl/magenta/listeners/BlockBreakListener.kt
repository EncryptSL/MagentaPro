package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.jail.JailPlayerEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class BlockBreakListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player

        val jailEvent = JailPlayerEvent(player, "ničit bloky")

        magenta.pluginManager.callEvent(jailEvent)

        if (jailEvent.isCancelled) {
            event.isCancelled = true
        }
    }

}