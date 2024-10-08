package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.kmono.lib.api.economy.EconomyTransactionResponse
import com.github.encryptsl.kmono.lib.api.economy.components.EconomyWithdraw
import com.github.encryptsl.kmono.lib.utils.BlockUtils
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.InfoType
import com.github.encryptsl.magenta.api.events.warp.*
import com.github.encryptsl.magenta.common.Permissions
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.math.BigDecimal

class WarpListeners(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onCreateWarp(event: WarpCreateEvent) {
        val warpName = event.warpName
        val player: Player = event.player
        val location: Location = event.location
        val cost =  magenta.commandHelper.trader.getCommandCost(player, "setwarp")

        if (!BlockUtils.isLocationSafe(location))
            return player.sendMessage(magenta.locale.translation("magenta.command.warp.error.safe.create"))

        magenta.warpModel.getWarpByName(warpName).thenAccept {
            player.sendMessage(magenta.locale.translation("magenta.command.warp.error.exist", Placeholder.parsed("warp", warpName)))
        }.exceptionally {
            magenta.warpModel.canSetWarp(player).thenAccept { canSetWarp ->
                if (!canSetWarp) {
                    player.sendMessage(magenta.locale.translation("magenta.command.warp.error.limit"))
                } else {
                    val response = EconomyWithdraw(player, "dollars", cost)
                        .transaction(magenta.vaultUnlockedHook)
                    if (response == EconomyTransactionResponse.ERROR_ENOUGH_BALANCE && cost != BigDecimal.ZERO)
                        return@thenAccept magenta.commandHelper.trader.notEnoughDollars(player)

                    if (response == EconomyTransactionResponse.SUCCESS || response == null || cost == BigDecimal.ZERO) {
                        magenta.commandHelper.trader.successTradeWithDraw(player, cost)
                        magenta.warpModel.creteWarp(player, location, warpName)
                        player.sendMessage(magenta.locale.translation("magenta.command.warp.success.created", Placeholder.parsed("warp", warpName)))
                    }
                }
            }
            return@exceptionally null
        }
    }

    @EventHandler
    fun onWarpDelete(event: WarpDeleteEvent) {
        val warpName = event.warpName
        val player: Player = event.player
        magenta.warpModel.getWarpByName(warpName).thenAccept {
            if (player.hasPermission(Permissions.WARPS_DELETE_OTHER)) {
                magenta.warpModel.deleteWarp(warpName)
                player.sendMessage(magenta.locale.translation("magenta.command.warp.success.deleted", Placeholder.parsed("warp", warpName)))
            } else {
                magenta.warpModel.deleteWarp(player.uniqueId, warpName)
                player.sendMessage(magenta.locale.translation("magenta.command.warp.success.deleted", Placeholder.parsed("warp", warpName)))
            }
        }.exceptionally {
            player.sendMessage(magenta.locale.translation("magenta.command.warp.error.not.exist", Placeholder.parsed("warp", warpName)))

            return@exceptionally null
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
                magenta.warpModel.getWarpByName(warpName).thenAccept { warp ->
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
                    return@exceptionally null
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

        magenta.warpModel.getWarpByName(warpName).thenAccept {
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

            return@exceptionally null
        }
    }

    @EventHandler
    fun onWarpRename(event: WarpRenameEvent) {
        val player = event.player
        val fromWarpName = event.fromWarpName
        val toWarpName = event.toWarpName
        magenta.warpModel.getWarpByName(fromWarpName).thenAccept {
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

            return@exceptionally null
        }
    }

    @EventHandler
    fun onWarpTeleport(event: WarpTeleportEvent) {
        val commandSender = event.commandSender
        val target = event.target
        val warpName = event.warpName

        val teleportSelfMessage = magenta.locale.translation("magenta.command.warp.success.teleport.self",
            Placeholder.parsed("warp", warpName))
        val teleportSelfMessageOther = magenta.locale.translation("magenta.command.warp.success.teleport.self.other",
            TagResolver.resolver(Placeholder.parsed("warp", warpName), Placeholder.parsed("target", target?.name ?: "")))

        magenta.warpModel.getWarpByName(warpName).thenAccept {
            val location = magenta.warpModel.toLocation(it)
            if (commandSender is Player) {
                teleportByPlayer(commandSender, target, location, teleportSelfMessage, teleportSelfMessageOther)
            } else {
                teleportByCommandSender(commandSender, target, location, teleportSelfMessage)
            }
        }.exceptionally {
            commandSender.sendMessage(it.message ?: it.localizedMessage)
            commandSender.sendMessage(magenta.locale.translation("magenta.command.warp.error.not.exist",
                Placeholder.parsed("warp", warpName)))
            return@exceptionally null
        }
    }

    private fun teleportByCommandSender(commandSender: CommandSender, target: Player?, location: Location, selfMessage: Component) {
        if (target != null) {
            if (!BlockUtils.isLocationSafe(location))
                return commandSender.sendMessage(magenta.locale.translation("magenta.command.warp.error.safe.teleport.self.other",
                    Placeholder.parsed("target", target.name)))

            target.teleportAsync(location)
            target.sendMessage(selfMessage)
        }
        commandSender.sendMessage(selfMessage)
    }

    private fun teleportByPlayer(player: Player, target: Player?, location: Location, selfMessage: Component, teleportSelfMessageOther: Component) {
        if (target == null) {
            if (!BlockUtils.isLocationSafe(location))
                return player.sendMessage(magenta.locale.translation("magenta.command.warp.error.safe.teleport.self"))

            player.teleportAsync(location)
            return player.sendMessage(selfMessage)
        }

        if (!player.hasPermission(Permissions.WARP_TELEPORT_OTHER)) return

        if (!BlockUtils.isLocationSafe(location))
            return player.sendMessage(magenta.locale.translation("magenta.command.warp.error.safe.teleport.self.other",
                Placeholder.parsed("target", target.name)))

        target.teleportAsync(location)
        target.sendMessage(selfMessage)
        player.sendMessage(teleportSelfMessageOther)
    }

}