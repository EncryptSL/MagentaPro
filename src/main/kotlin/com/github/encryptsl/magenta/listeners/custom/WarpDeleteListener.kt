package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.warp.WarpDeleteEvent
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class WarpDeleteListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onWarpDelete(event: WarpDeleteEvent) {
        val warpName = event.warpName
        val player: Player = event.player

        if (!magenta.warpModel.getWarpExist(warpName))
            return player.sendMessage(
                ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.warp.error.not.exist"), TagResolver.resolver(
                Placeholder.parsed("warp", warpName)
            )))

        if (player.hasPermission("magenta.warp.delete.other"))
            magenta.warpModel.deleteWarp(warpName)
        else
            magenta.warpModel.deleteWarp(player, warpName)

        player.sendMessage(ModernText.miniModernText("magenta.command.warp.success.deleted", TagResolver.resolver(
            Placeholder.parsed("warp", warpName)
        )))
    }

}