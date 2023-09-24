package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.warp.WarpRenameEvent
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class WarpRenameListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onWarpRename(event: WarpRenameEvent) {
        val player = event.player
        val fromWarpName = event.fromWarpName
        val toWarpName = event.toWarpName

        if (!magenta.warpModel.getWarpExist(fromWarpName))
            return player.sendMessage(
                ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.warp.error.not.exist"),
                    TagResolver.resolver(Placeholder.parsed("warp", fromWarpName))))


        if (player.hasPermission("magenta.warp.rename.other"))
            magenta.warpModel.renameWarp(fromWarpName, toWarpName)
        else
            magenta.warpModel.renameWarp(player, fromWarpName, toWarpName)

        player.sendMessage(ModernText.miniModernText("magenta.command.warp.success.renamed", TagResolver.resolver(
            Placeholder.parsed("from_warp", fromWarpName),
            Placeholder.parsed("to_warp", toWarpName)
        )))
    }

}