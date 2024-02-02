package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.InfoType
import com.github.encryptsl.magenta.api.events.jail.*
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotation.specifier.Greedy
import org.incendo.cloud.annotations.*

@Suppress("UNUSED")
@CommandDescription("Provived by plugin MagentaPro")
class JailCmd(private val magenta: Magenta) {

    @Command("jail info <name>")
    @Permission("magenta.jail.info")
    fun onJailInfo(commandSender: CommandSender, @Argument(value = "name", suggestions = "jails") jailName: String) {
        magenta.schedulerMagenta.doSync(magenta) {
            magenta.pluginManager.callEvent(JailInfoEvent(commandSender, jailName, InfoType.INFO))
        }
    }

    @Command("jail player <jailName> <player> [time] [reason]")
    @Permission("magenta.jail")
    fun onJailPlayer(
        commandSender: CommandSender,
        @Argument(value = "jailName", suggestions = "jails") jailName: String,
        @Argument(value = "player", suggestions = "offlinePlayers") offlinePlayer: OfflinePlayer,
        @Argument(value = "time") time: Long = 120,
        @Argument(value = "reason") @Greedy reason: String = "Protože bagr ?!"
    ) {
        magenta.schedulerMagenta.doSync(magenta) {
            magenta.pluginManager.callEvent(JailEvent(commandSender, jailName, offlinePlayer, time, reason))
        }
    }

    @Command("jail pardon <player>")
    @Permission("magenta.jail.pardon")
    fun onJailPardon(
        commandSender: CommandSender,
        @Argument(value = "player", suggestions = "offlinePlayers") offlinePlayer: OfflinePlayer,
    ) {
        magenta.schedulerMagenta.doSync(magenta) {
            magenta.pluginManager.callEvent(JailPardonEvent(offlinePlayer))
        }
    }


    @Command("jail create <name>")
    @Permission("magenta.jail.create")
    fun onJailCreate(player: Player, @Argument(value = "name") jailName: String) {
        magenta.schedulerMagenta.doSync(magenta) {
            magenta.pluginManager.callEvent(JailCreateEvent(player, jailName, player.location))
        }
    }

    @Command("jail tp <name> [player]")
    @Permission("magenta.jail.tp")
    fun onJailTp(commandSender: CommandSender, @Argument(value = "name", suggestions = "jails") jailName: String, @Argument(value = "player", suggestions = "players") target: Player?) {

        if (commandSender is Player) {
            if (target == null) {
                commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.jail.success.teleport"), TagResolver.resolver(
                    Placeholder.parsed("jail", jailName)
                )))

                magenta.schedulerMagenta.doSync(magenta) {
                    magenta.pluginManager.callEvent(JailTeleportEvent(commandSender, magenta.jailManager.getJailLocation(jailName)))
                }
                return
            }
            target.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.jail.success.teleport"), TagResolver.resolver(
                Placeholder.parsed("jail", jailName)
            )))

            commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.jail.success.teleport.to"), TagResolver.resolver(
                Placeholder.parsed("jail", jailName),
                Placeholder.parsed("player", target.name)
            )))

            magenta.schedulerMagenta.doSync(magenta) {
                magenta.pluginManager.callEvent(JailTeleportEvent(commandSender, magenta.jailManager.getJailLocation(jailName)))
            }
        } else {
            if (target == null) return

            target.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.jail.success.teleport"), TagResolver.resolver(
                Placeholder.parsed("jail", jailName)
            )))

            commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.jail.success.teleport.to"), TagResolver.resolver(
                Placeholder.parsed("jail", jailName),
                Placeholder.parsed("player", target.name)
            )))

            magenta.schedulerMagenta.doSync(magenta) {
                magenta.pluginManager.callEvent(JailTeleportEvent(target, magenta.jailManager.getJailLocation(jailName)))
            }
        }

    }

    @Command("jail delete <name>")
    @Permission("magenta.jail.delete")
    fun onJailDelete(commandSender: CommandSender, @Argument(value = "name", suggestions = "jails") jailName: String) {
        magenta.schedulerMagenta.doSync(magenta) {
            magenta.pluginManager.callEvent(JailDeleteEvent(commandSender, jailName))
        }
    }

    @ProxiedBy("jails")
    @Command("jail list")
    @Permission("magenta.jail.list")
    fun onJails(commandSender: CommandSender) {
        magenta.schedulerMagenta.doSync(magenta) {
            magenta.pluginManager.callEvent(JailInfoEvent(commandSender, null, InfoType.LIST))
        }
    }

}