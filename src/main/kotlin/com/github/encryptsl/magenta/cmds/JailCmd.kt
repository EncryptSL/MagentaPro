package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.*
import cloud.commandframework.annotations.specifier.Greedy
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.InfoType
import com.github.encryptsl.magenta.api.account.PlayerAccount
import com.github.encryptsl.magenta.api.events.jail.*
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandDescription("Provived by plugin MagentaPro")
class JailCmd(private val magenta: Magenta) {

    @CommandMethod("jail info <name>")
    @CommandPermission("magenta.jail.info")
    fun onJailInfo(commandSender: CommandSender, @Argument(value = "name", suggestions = "jails") jailName: String) {

    }

    @CommandMethod("jail player <jailName> <player> [time] [reason]")
    @CommandPermission("magenta.jail")
    fun onJailPlayer(
        commandSender: CommandSender,
        @Argument(value = "jailName", suggestions = "jails") jailName: String,
        @Argument(value = "player", suggestions = "offlinePlayers") offlinePlayer: OfflinePlayer,
        @Argument(value = "time", defaultValue = "120") time: Long,
        @Argument(value = "reason", defaultValue = "Protože bagr ?!") @Greedy reason: String
    ) {
        magenta.schedulerMagenta.runTask(magenta) {
            magenta.pluginManager.callEvent(JailEvent(commandSender, jailName, offlinePlayer, time, reason))
        }
    }

    @CommandMethod("jail pardon <player>")
    @CommandPermission("magenta.jail.pardon")
    fun onJailPardon(
        commandSender: CommandSender,
        @Argument(value = "player", suggestions = "offlinePlayers") offlinePlayer: OfflinePlayer,
    ) {
        magenta.schedulerMagenta.runTask(magenta) {
            magenta.pluginManager.callEvent(JailPardonEvent(offlinePlayer))
        }
    }


    @CommandMethod("jail create <name>")
    @CommandPermission("magenta.jail.create")
    fun onJailCreate(player: Player, @Argument(value = "name") jailName: String) {
        magenta.schedulerMagenta.runTask(magenta) {
            magenta.pluginManager.callEvent(JailCreateEvent(player, jailName, player.location))
        }
    }

    @CommandMethod("jail tp <name> [player]")
    @CommandPermission("magenta.jail.tp")
    fun onJailTp(commandSender: CommandSender, @Argument(value = "name", suggestions = "jails") jailName: String, @Argument(value = "player", suggestions = "players") target: Player?) {

        if (commandSender is Player) {
            if (target == null) {
                val playerAccount = PlayerAccount(magenta, commandSender.uniqueId)
                commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.jail.success.teleport"), TagResolver.resolver(
                    Placeholder.parsed("jail", jailName)
                )))

                magenta.schedulerMagenta.runTask(magenta) {
                    magenta.pluginManager.callEvent(JailTeleportEvent(commandSender, playerAccount.jailManager.getJailLocation(jailName)))
                }
                return
            }
            val targetAccount = PlayerAccount(magenta, target.uniqueId)

            target.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.jail.success.teleport"), TagResolver.resolver(
                Placeholder.parsed("jail", jailName)
            )))

            commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.jail.success.teleport.to"), TagResolver.resolver(
                Placeholder.parsed("jail", jailName),
                Placeholder.parsed("player", target.name)
            )))

            magenta.schedulerMagenta.runTask(magenta) {
                magenta.pluginManager.callEvent(JailTeleportEvent(commandSender, targetAccount.jailManager.getJailLocation(jailName)))
            }
        } else {
            if (target == null) return

            val targetAccount = PlayerAccount(magenta, target.uniqueId)
            target.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.jail.success.teleport"), TagResolver.resolver(
                Placeholder.parsed("jail", jailName)
            )))

            commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.jail.success.teleport.to"), TagResolver.resolver(
                Placeholder.parsed("jail", jailName),
                Placeholder.parsed("player", target.name)
            )))

            magenta.schedulerMagenta.runTask(magenta) {
                magenta.pluginManager.callEvent(JailTeleportEvent(target, targetAccount.jailManager.getJailLocation(jailName)))
            }
        }

    }

    @CommandMethod("jail delete <name>")
    @CommandPermission("magenta.jail.delete")
    fun onJailDelete(commandSender: CommandSender, @Argument(value = "name", suggestions = "jails") jailName: String) {
        magenta.schedulerMagenta.runTask(magenta) {
            magenta.pluginManager.callEvent(JailDeleteEvent(commandSender, jailName))
        }
    }

    @ProxiedBy("jails")
    @CommandMethod("jail list")
    @CommandPermission("magenta.jail.list")
    fun onJails(commandSender: CommandSender) {
        magenta.schedulerMagenta.runTask(magenta) {
            magenta.pluginManager.callEvent(JailInfoEvent(commandSender, null, InfoType.LIST))
        }
    }

}