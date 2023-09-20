package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.CommandHelper
import com.github.encryptsl.magenta.common.utils.ModernText
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandDescription("Provided by plugin MagentaPro")
class TpCmd(private val magenta: Magenta) {

    private val commandHelper = CommandHelper(magenta)

    @CommandMethod("tpa <target>")
    @CommandPermission("magenta.tpa")
    fun onTpa(player: Player, @Argument(value = "target", suggestions = "players") target: Player) {

    }

    @CommandMethod("tpaccept")
    @CommandPermission("magenta.tpaccept")
    fun onTpaAccept(player: Player) {

    }

    @CommandMethod("tp <target>")
    @CommandPermission("magenta.tp")
    fun onTeleport(player: Player, @Argument(value = "target", suggestions = "players") target: Player) {
        player.teleport(target)
        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("")))
    }

    @CommandMethod("tp <player> <target>")
    @CommandPermission("magenta.tp.other")
    fun onTeleportConsole(commandSender: CommandSender, @Argument(value = "player", suggestions = "players") player: Player, @Argument(value = "target", suggestions = "players") target: Player) {
        player.teleport(target.location)
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("")))
    }

    @CommandMethod("tpo <target>")
    @CommandPermission("magenta.tpo")
    fun onTeleportOfflineLocation(player: Player, @Argument(value = "player", suggestions = "offlinePlayers") offlinePlayer: OfflinePlayer) {
        commandHelper.teleportOffline(player, offlinePlayer)
    }

    @CommandMethod("tphere [player]")
    @CommandPermission("magenta.tphere")
    fun onTeleportHere(player: Player, @Argument(value = "player") target: Player?) {

        if (target == null) {
            commandHelper.teleportAll(player, Bukkit.getOnlinePlayers())
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("")))
            return
        }

        target.teleport(player)
        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("")))
    }

    @CommandMethod("tpall")
    @CommandPermission("magenta.tpall")
    fun onTeleportAllHere(player: Player) {
        commandHelper.teleportAll(player, Bukkit.getOnlinePlayers())
        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("")))
    }

}