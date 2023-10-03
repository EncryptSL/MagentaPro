package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.account.PlayerAccount
import com.github.encryptsl.magenta.api.events.teleport.TpaAcceptEvent
import com.github.encryptsl.magenta.api.events.teleport.TpaDenyEvent
import com.github.encryptsl.magenta.api.events.teleport.TpaRequestEvent
import com.github.encryptsl.magenta.common.CommandHelper
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class TpCmd(private val magenta: Magenta) {

    private val commandHelper = CommandHelper(magenta)

    @CommandMethod("tpa <target>")
    @CommandPermission("magenta.tpa")
    fun onTpa(player: Player, @Argument(value = "target", suggestions = "players") target: Player) {
        magenta.schedulerMagenta.runTask(magenta) {
            magenta.pluginManager.callEvent(TpaRequestEvent(player, target))
        }
    }

    @CommandMethod("tpaccept")
    @CommandPermission("magenta.tpaccept")
    fun onTpaAccept(player: Player) {
        magenta.schedulerMagenta.runTask(magenta) {
            magenta.pluginManager.callEvent(TpaAcceptEvent(player))
        }
    }

    @CommandMethod("tpadeny")
    @CommandPermission("magenta.tpadeny")
    fun onTpaDeny(player: Player) {
        magenta.schedulerMagenta.runTask(magenta) {
            magenta.pluginManager.callEvent(TpaDenyEvent(player))
        }
    }


    @CommandMethod("tp <player>")
    @CommandPermission("magenta.tp")
    fun onTeleport(player: Player, @Argument(value = "player", suggestions = "players") target: Player) {
        val account = PlayerAccount(magenta, target.uniqueId)
        if (account.getAccount().getBoolean("teleportenabled"))
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.tp.error.exempt"), TagResolver.resolver(
                Placeholder.parsed("player", player.name)
            )))

        magenta.schedulerMagenta.runTask(magenta) {
            player.teleport(target)
        }
        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.tp.success.self"), TagResolver.resolver(
            Placeholder.parsed("target", target.name)
        )))
    }

    @CommandMethod("tp <player> <target>")
    @CommandPermission("magenta.tp.other")
    fun onTeleportConsole(commandSender: CommandSender, @Argument(value = "player", suggestions = "players") player: Player, @Argument(value = "target", suggestions = "players") target: Player) {
        val playerAccount = PlayerAccount(magenta, player.uniqueId)
        if (playerAccount.getAccount().getBoolean("teleportenabled"))
            return commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.tp.error.exempt"), TagResolver.resolver(
                Placeholder.parsed("player", player.name)
            )))

        val targetAccount = PlayerAccount(magenta, target.uniqueId)
        if (targetAccount.getAccount().getBoolean("teleportenabled"))
            return commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.tp.error.exempt"), TagResolver.resolver(
                Placeholder.parsed("player", player.name)
            )))

        magenta.schedulerMagenta.runTask(magenta) {
            player.teleport(target.location)
        }
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.tp.success.to"), TagResolver.resolver(
            Placeholder.parsed("player", player.name),
            Placeholder.parsed("target", target.name)
        )))
        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.tp.success.self"), TagResolver.resolver(
            Placeholder.parsed("target", target.name)
        )))
    }

    @CommandMethod("tpo <target>")
    @CommandPermission("magenta.tpo")
    fun onTeleportOfflineLocation(player: Player, @Argument(value = "target", suggestions = "offlinePlayers") offlinePlayer: OfflinePlayer) {
        magenta.schedulerMagenta.runTask(magenta) {
            commandHelper.teleportOffline(player, offlinePlayer)
        }
    }

    @CommandMethod("tphere [target]")
    @CommandPermission("magenta.tphere")
    fun onTeleportHere(player: Player, @Argument(value = "target") target: Player?) {

        if (target == null) {
            magenta.schedulerMagenta.runTask(magenta) {
                commandHelper.teleportAll(player, Bukkit.getOnlinePlayers())
            }
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("")))
            return
        }

        magenta.schedulerMagenta.runTask(magenta) {
            target.teleport(player)
        }
        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("")))
    }

    @CommandMethod("tpall")
    @CommandPermission("magenta.tpall")
    fun onTeleportAllHere(player: Player) {
        magenta.schedulerMagenta.runTask(magenta) {
            commandHelper.teleportAll(player, Bukkit.getOnlinePlayers())
        }
        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("")))
    }

}