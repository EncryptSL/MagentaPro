package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.InfoType
import com.github.encryptsl.magenta.api.events.warp.*
import com.github.encryptsl.magenta.common.database.tables.WarpTable
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class WarpListeners(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onCreateWarp(event: WarpCreateEvent) {
        val warpName = event.warpName
        val player: Player = event.player
        val location: Location = event.location

        if (magenta.warpModel.getWarpExist(warpName))
            return player.sendMessage(
                ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.warp.error.exist"), TagResolver.resolver(
                Placeholder.parsed("warp", warpName)
            )))

        if (!magenta.warpModel.canSetWarp(player))
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.warp.error.limit")))

        magenta.warpModel.creteWarp(player, location, warpName)
        player.sendMessage(
            ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.warp.success.created"), TagResolver.resolver(
            Placeholder.parsed("warp", warpName)
        )))
    }

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

        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.warp.success.deleted"), TagResolver.resolver(
            Placeholder.parsed("warp", warpName)
        )))
    }

    @EventHandler
    fun onWarpInfo(event: WarpInfoEvent) {
        val commandSender = event.commandSender
        val warpInfoType = event.infoType

        when(warpInfoType) {
            InfoType.LIST -> {
                val list = magenta.warpModel.getWarps().joinToString { s ->
                    magenta.localeConfig.getMessage("magenta.command.warp.success.list.component")
                        .replace("<warp>", s.warpName)
                        .replace("<info>", magenta.config.getString("warp-info-format").toString()
                            .replace("<warp>", s.warpName)
                            .replace("<owner>", s.owner)
                            .replace("<x>", s.x.toString())
                            .replace("<y>", s.y.toString())
                            .replace("<z>", s.z.toString())
                            .replace("<world>", s.world)
                        )
                }
                commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.warp.success.list"), TagResolver.resolver(
                    Placeholder.component("warps", ModernText.miniModernText(list))
                )))
            }
            InfoType.INFO -> {
                val warpName = event.warpName ?: return
                if (!magenta.warpModel.getWarpExist(warpName))
                    return commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.warp.error.not.exist"),
                        Placeholder.parsed("warp", warpName)
                    ))

                magenta.config.getStringList("warp-info-format").forEach { s ->
                    commandSender.sendMessage(ModernText.miniModernText(s, TagResolver.resolver(
                        Placeholder.parsed("warp", magenta.warpModel.getWarp(warpName, WarpTable.warpName)),
                        Placeholder.parsed("owner", magenta.warpModel.getWarp(warpName, WarpTable.username)),
                        Placeholder.parsed("world", magenta.warpModel.getWarp(warpName, WarpTable.world)),
                        Placeholder.parsed("x", magenta.warpModel.getWarp(warpName, WarpTable.x).toString()),
                        Placeholder.parsed("y", magenta.warpModel.getWarp(warpName, WarpTable.y).toString()),
                        Placeholder.parsed("z", magenta.warpModel.getWarp(warpName, WarpTable.z).toString()),
                    )))
                }
            }
        }
    }

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

    @EventHandler
    fun onWarpRename(event: WarpRenameEvent) {
        val player = event.player
        val fromWarpName = event.fromWarpName
        val toWarpName = event.toWarpName

        if (!magenta.warpModel.getWarpExist(fromWarpName))
            return player.sendMessage(
                ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.warp.error.not.exist"),
                    TagResolver.resolver(Placeholder.parsed("warp", fromWarpName))))


        if (player.hasPermission("magenta.rename.warp.other"))
            magenta.warpModel.renameWarp(fromWarpName, toWarpName)
        else
            magenta.warpModel.renameWarp(player, fromWarpName, toWarpName)

        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.warp.success.renamed"), TagResolver.resolver(
            Placeholder.parsed("from_warp", fromWarpName),
            Placeholder.parsed("to_warp", toWarpName)
        )))
    }

    @EventHandler
    fun onWarpTeleport(event: WarpTeleportEvent) {
        val commandSender = event.commandSender
        val target = event.target
        val warpName = event.warpName

        if (!magenta.warpModel.getWarpExist(warpName))
            return commandSender.sendMessage(
                ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.warp.error.not.exist"),
                    TagResolver.resolver(Placeholder.parsed("warp", warpName))))

        try {
            val (_: String, _: String, _: String, world: String, x: Int, y: Int, z: Int, pitch: Float, yaw: Float) = magenta.warpModel.getWarps().first { s -> s.warpName == warpName}

            val location = Location(Bukkit.getWorld(world), x.toDouble(), y.toDouble(), z.toDouble(), yaw, pitch)
            val teleportSelfMessage = magenta.localeConfig.getMessage("magenta.command.warp.success.teleport.self")
            val teleportMessageTo = magenta.localeConfig.getMessage("magenta.command.warp.success.teleport.to")

            if (commandSender is Player) {
                if (target == null) {
                    commandSender.teleport(location)
                    commandSender.sendMessage(ModernText.miniModernText(teleportSelfMessage, TagResolver.resolver(
                        Placeholder.parsed("warp", warpName)
                    )))
                    return
                }

                if (!commandSender.hasPermission("magenta.warp.other")) return

                target.teleport(location)
                target.sendMessage(ModernText.miniModernText(teleportSelfMessage, Placeholder.parsed("warp", warpName)))
                return commandSender.sendMessage(ModernText.miniModernText(teleportMessageTo, Placeholder.parsed("warp", warpName)))
            }

            if (target != null) {
                target.teleport(location)
                target.sendMessage(ModernText.miniModernText(teleportSelfMessage, Placeholder.parsed("warp", warpName)))
            }
            commandSender.sendMessage(ModernText.miniModernText(teleportMessageTo, TagResolver.resolver(Placeholder.parsed("warp", warpName))))
        } catch (e : Exception) {
            magenta.logger.severe(e.message ?: e.localizedMessage)
        }
    }


}