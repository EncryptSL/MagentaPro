package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.warp.WarpMoveLocationEvent
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class WarpMoveLocationListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onWarpMoveLocation(event: WarpMoveLocationEvent) {
        val player: Player = event.player
        val location: Location = player.location
        val warpName: String = event.warpName

        if (!magenta.warpModel.getWarpExist(warpName))
            return player.sendMessage(
                ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.warp.error.not.exist"),
                    TagResolver.resolver(Placeholder.parsed("warp", warpName))))


        if (player.hasPermission("magenta.move.warp.other"))
            magenta.warpModel.moveWarp(warpName, location)
        else
            magenta.warpModel.moveWarp(player, warpName, location)

        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.warp.success.moved"), TagResolver.resolver(
            Placeholder.parsed("warp", warpName),
            Placeholder.parsed("x", location.x.toString()),
            Placeholder.parsed("y", location.z.toString()),
            Placeholder.parsed("z", location.y.toString())
        )))
    }

}