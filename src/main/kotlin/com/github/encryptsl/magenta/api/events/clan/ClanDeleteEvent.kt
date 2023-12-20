package com.github.encryptsl.magenta.api.events.clan

import org.bukkit.command.CommandSender
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class ClanDeleteEvent(val commandSender: CommandSender, val clanName: String) : Event(), Cancellable {
    private var isCancelled: Boolean = false
    override fun getHandlers(): HandlerList = handlerList
    override fun isCancelled(): Boolean = isCancelled
    override fun setCancelled(cancel: Boolean) {
        this.isCancelled = cancel
    }

    companion object {
        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = handlerList
    }
}