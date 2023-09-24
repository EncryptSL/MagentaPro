package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.teleport.TpaAcceptEvent
import com.github.encryptsl.magenta.common.TpaRequestManager
import com.github.encryptsl.magenta.common.utils.ModernText
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class TpaAcceptListener(private val magenta: Magenta) : Listener {

    private val tpaRequestManager = TpaRequestManager(magenta)

    @EventHandler
    fun onTpaAccept(event: TpaAcceptEvent) {
        val player = event.player

        if (!tpaRequestManager.acceptRequest(player))
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.tpa.error.accept")))

        tpaRequestManager.acceptRequest(player)
    }

}