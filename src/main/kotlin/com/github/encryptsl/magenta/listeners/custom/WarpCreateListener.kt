package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.warp.WarpCreateEvent
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class WarpCreateListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onCreateWarp(event: WarpCreateEvent) {
        val warpName = event.warpName
        val player: Player = event.player
        val location: Location = event.location

        if (magenta.warpModel.getWarpExist(warpName))
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.warp.error.exist"), TagResolver.resolver(
                Placeholder.parsed("warp", warpName)
            )))

        if (!magenta.warpModel.canSetWarp(player))
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.warp.error.limit")))

        magenta.warpModel.creteWarp(player, location, warpName)
        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.warp.success.created"), TagResolver.resolver(
            Placeholder.parsed("warp", warpName)
        )))
    }

}