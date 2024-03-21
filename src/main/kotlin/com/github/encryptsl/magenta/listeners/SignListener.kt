package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ModernText
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.SignChangeEvent

class SignListener(private val magenta: Magenta) : Listener {
    @EventHandler
    fun onChangeWarpSign(event: SignChangeEvent) {
        val player = event.player

        val line = event.line(0)

        if (ModernText.convertComponentToText(line!!).equals(magenta.localeConfig.getMessage("magenta.sign.warp.required"), true)) {
            if (!player.hasPermission("magenta.sign.warp.place")) {
                event.block.breakNaturally()
                return
            }
            event.line(0, magenta.localeConfig.translation("magenta.sign.warp"))
            player.sendMessage(magenta.localeConfig.translation("magenta.sign.warp.success.created"))
        }
    }
}