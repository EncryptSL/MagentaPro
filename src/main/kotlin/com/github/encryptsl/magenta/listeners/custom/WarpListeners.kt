package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.kmono.lib.utils.BlockUtils
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.InfoType
import com.github.encryptsl.magenta.api.events.warp.*
import com.github.encryptsl.magenta.common.Permissions
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
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

        if (!BlockUtils.isLocationSafe(location))
            return player.sendMessage(magenta.locale.translation("magenta.command.warp.error.safe.create"))

        magenta.warpModel.getWarpByName(warpName).thenApply {
            player.sendMessage(magenta.locale.translation("magenta.command.warp.error.exist", Placeholder.parsed("warp", warpName)))
        }.exceptionally {
            magenta.warpModel.canSetWarp(player).thenAccept { canSetWarp ->
                if (!canSetWarp) {
                    player.sendMessage(magenta.locale.translation("magenta.command.warp.error.limit"))
                } else {
                    magenta.warpModel.creteWarp(player, location, warpName)
                    player.sendMessage(magenta.locale.translation("magenta.command.warp.success.created", Placeholder.parsed("warp", warpName)))
                }
            }
        }
    }

    @EventHandler
    fun onWarpDelete(event: WarpDeleteEvent) {
        val warpName = event.warpName
        val player: Player = event.player
        magenta.warpModel.getWarpByName(warpName).thenApply {
            if (player.hasPermission(Permissions.WARPS_DELETE_OTHER))
                magenta.warpModel.deleteWarp(warpName)
            else
                magenta.warpModel.deleteWarp(player.uniqueId, warpName)
        }.exceptionally {
            player.sendMessage(magenta.locale.translation("magenta.command.warp.error.not.exist", Placeholder.parsed("warp", warpName)))
        }
    }

    @EventHandler
    fun onWarpInfo(event: WarpInfoEvent) {
        val commandSender = event.commandSender
        val warpInfoType = event.infoType

        when(warpInfoType) {
            InfoType.LIST -> {
                val list = magenta.warpModel.getWarps().join().joinToString { s ->
                    magenta.locale.getMessage("magenta.command.warp.success.list.component")
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
                commandSender.sendMessage(magenta.locale.translation("magenta.command.warp.success.list",
                    Placeholder.component("warps", ModernText.miniModernText(list))
                ))
            }
            InfoType.INFO -> {
                val warpName = event.warpName ?: return
                magenta.warpModel.getWarpByName(warpName).thenApply { warp ->
                    val warpInfoFormat = magenta.config.getString("warp-info-format").toString()
                    commandSender.sendMessage(ModernText.miniModernText(warpInfoFormat, TagResolver.resolver(
                        Placeholder.parsed("warp", warp.warpName),
                        Placeholder.parsed("owner", warp.owner),
                        Placeholder.parsed("world", warp.world),
                        Placeholder.parsed("x", warp.x.toString()),
                        Placeholder.parsed("y", warp.y.toString()),
                        Placeholder.parsed("z", warp.z.toString()))
                    ))
                }.exceptionally {
                    commandSender.sendMessage(magenta.locale.translation("magenta.command.warp.error.not.exist",
                        Placeholder.parsed("warp", warpName))
                    )
                }
            }
        }
    }

    @EventHandler
    fun onWarpMoveLocation(event: WarpMoveLocationEvent) {
        val player: Player = event.player
        val location: Location = player.location
        val warpName: String = event.warpName

        if (!BlockUtils.isLocationSafe(location))
            return player.sendMessage(magenta.locale.translation("magenta.command.warp.error.safe.move"))

        magenta.warpModel.getWarpByName(warpName).thenApply {
            if (player.hasPermission(Permissions.WARPS_MOVE_OTHER))
                magenta.warpModel.moveWarp(warpName, location)
            else
                magenta.warpModel.moveWarp(player.uniqueId, warpName, location)

            player.sendMessage(magenta.locale.translation("magenta.command.warp.success.moved", TagResolver.resolver(
                Placeholder.parsed("warp", warpName),
                Placeholder.parsed("x", location.x.toString()),
                Placeholder.parsed("y", location.z.toString()),
                Placeholder.parsed("z", location.y.toString())
            )))
        }.exceptionally {
            player.sendMessage(magenta.locale.translation("magenta.command.warp.error.not.exist", Placeholder.parsed("warp", warpName)))
        }
    }

    @EventHandler
    fun onWarpRename(event: WarpRenameEvent) {
        val player = event.player
        val fromWarpName = event.fromWarpName
        val toWarpName = event.toWarpName
        magenta.warpModel.getWarpByName(fromWarpName).thenApply {
            if (player.hasPermission(Permissions.WARPS_RENAME_OTHER))
                magenta.warpModel.renameWarp(fromWarpName, toWarpName)
            else
                magenta.warpModel.renameWarp(player.uniqueId, fromWarpName, toWarpName)

            player.sendMessage(magenta.locale.translation("magenta.command.warp.success.renamed", TagResolver.resolver(
                Placeholder.parsed("from_warp", fromWarpName),
                Placeholder.parsed("to_warp", toWarpName)
            )))
        }.exceptionally {
            player.sendMessage(magenta.locale.translation("magenta.command.warp.error.not.exist", Placeholder.parsed("warp", fromWarpName)))
        }
    }

    @EventHandler
    fun onWarpTeleport(event: WarpTeleportEvent) {
        val commandSender = event.commandSender
        val target = event.target
        val warpName = event.warpName

        if (!magenta.warpModel.getWarpExist(warpName).join())
            return commandSender.sendMessage(magenta.locale.translation("magenta.command.warp.error.not.exist",
                   Placeholder.parsed("warp", warpName)))

        magenta.warpModel.getWarpByName(warpName).thenApply {
            val location = magenta.warpModel.toLocation(warpName)
            val teleportSelfMessage = magenta.locale.translation("magenta.command.warp.success.teleport.self",
                Placeholder.parsed("warp", warpName))
            val teleportSelfMessageOther = magenta.locale.translation("magenta.command.warp.success.teleport.self.other",
                TagResolver.resolver(Placeholder.parsed("warp", warpName), Placeholder.parsed("target", target?.name ?: "")))

            if (commandSender is Player) {
                if (target == null) {
                    if (!BlockUtils.isLocationSafe(magenta.warpModel.toLocation(warpName)))
                        return@thenApply commandSender.sendMessage(magenta.locale.translation("magenta.command.warp.error.safe.teleport.self"))

                    commandSender.teleport(location)
                    commandSender.sendMessage(teleportSelfMessage)
                    return@thenApply
                }

                if (!commandSender.hasPermission(Permissions.WARP_TELEPORT_OTHER)) return@thenApply

                if (!BlockUtils.isLocationSafe(magenta.warpModel.toLocation(warpName)))
                    return@thenApply commandSender.sendMessage(magenta.locale.translation("magenta.command.warp.error.safe.teleport.self.other",
                        Placeholder.parsed("target", target.name)))

                target.teleport(location)
                target.sendMessage(teleportSelfMessage)
                return@thenApply commandSender.sendMessage(teleportSelfMessageOther)
            }

            if (target != null) {
                if (!BlockUtils.isLocationSafe(magenta.warpModel.toLocation(warpName)))
                    return@thenApply commandSender.sendMessage(magenta.locale.translation("magenta.command.warp.error.safe.teleport.self.other",
                        Placeholder.parsed("target", target.name)))

                target.teleport(location)
                target.sendMessage(teleportSelfMessage)
            }
            commandSender.sendMessage(teleportSelfMessage)
        }.exceptionally {
            commandSender.sendMessage(magenta.locale.translation("magenta.command.warp.error.not.exist",
                Placeholder.parsed("warp", warpName)))
        }
    }

}