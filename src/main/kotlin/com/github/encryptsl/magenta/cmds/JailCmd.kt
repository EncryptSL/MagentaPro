package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.InfoType
import com.github.encryptsl.magenta.api.commands.AnnotationFeatures
import com.github.encryptsl.magenta.api.events.jail.*
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotation.specifier.Greedy
import org.incendo.cloud.annotations.*
import org.incendo.cloud.paper.PaperCommandManager
import org.incendo.cloud.suggestion.Suggestion
import java.util.concurrent.CompletableFuture

@Suppress("UNUSED", "UNUSED_PARAMETER")
class JailCmd(private val magenta: Magenta) : AnnotationFeatures {

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: PaperCommandManager<CommandSender>
    ) {
        commandManager.parserRegistry().registerSuggestionProvider("jails") { _, _ ->
            return@registerSuggestionProvider CompletableFuture.completedFuture(
                magenta.jailConfig.getConfig().getConfigurationSection("jails")
                    ?.getKeys(false)
                    ?.mapNotNull { Suggestion.suggestion(it.toString()) }!!
            )
        }
        annotationParser.parse(this)
    }

    @Command("jail info <name>")
    @Permission("magenta.jail.info")
    @CommandDescription("This command send info about jail")
    fun onJailInfo(commandSender: CommandSender, @Argument(value = "name", suggestions = "jails") jailName: String) {
        magenta.pluginManager.callEvent(JailInfoEvent(commandSender, jailName, InfoType.INFO))
    }

    @Command("jail player <jailName> <player> [time] [reason]")
    @Permission("magenta.jail")
    @CommandDescription("This command send player into jail")
    fun onJailPlayer(
        commandSender: CommandSender,
        @Argument(value = "jailName", suggestions = "jails") jailName: String,
        @Argument(value = "player", suggestions = "offlinePlayers") offlinePlayer: OfflinePlayer,
        @Argument(value = "time") time: Long = 120,
        @Argument(value = "reason") @Default("Protože Bagr ?!") @Greedy reason: String
    ) {
        magenta.pluginManager.callEvent(JailEvent(commandSender, jailName, offlinePlayer, time, reason))
    }

    @Command("jail pardon <player>")
    @Permission("magenta.jail.pardon")
    @CommandDescription("This command release from jail")
    fun onJailPardon(
        commandSender: CommandSender,
        @Argument(value = "player", suggestions = "offlinePlayers") offlinePlayer: OfflinePlayer,
    ) {
        magenta.pluginManager.callEvent(JailPardonEvent(offlinePlayer))
    }


    @Command("jail create <name>")
    @Permission("magenta.jail.create")
    @CommandDescription("This command create jail where you stand")
    fun onJailCreate(player: Player, @Argument(value = "name") jailName: String) {
        magenta.pluginManager.callEvent(JailCreateEvent(player, jailName, player.location))
    }

    @Command("jail tp <name> [player]")
    @Permission("magenta.jail.tp")
    @CommandDescription("This command teleport you into jail")
    fun onJailTp(commandSender: CommandSender, @Argument(value = "name", suggestions = "jails") jailName: String, @Argument(value = "player", suggestions = "players") target: Player?) {

        if (commandSender is Player) {
            if (target == null) {
                commandSender.sendMessage(magenta.locale.translation("magenta.command.jail.success.teleport",
                    Placeholder.parsed("jail", jailName)
                ))

                val location = magenta.jailManager.getJailLocation(jailName)
                location?.let { magenta.pluginManager.callEvent(JailTeleportEvent(commandSender, it)) }
                return
            }
            target.sendMessage(magenta.locale.translation("magenta.command.jail.success.teleport",
                Placeholder.parsed("jail", jailName)
            ))

            commandSender.sendMessage(magenta.locale.translation("magenta.command.jail.success.teleport.to", TagResolver.resolver(
                Placeholder.parsed("jail", jailName),
                Placeholder.parsed("player", target.name)
            )))

            val location = magenta.jailManager.getJailLocation(jailName)
            location?.let { magenta.pluginManager.callEvent(JailTeleportEvent(commandSender, it)) }
        } else {
            if (target == null) return

            target.sendMessage(magenta.locale.translation("magenta.command.jail.success.teleport", Placeholder.parsed("jail", jailName)))

            commandSender.sendMessage(magenta.locale.translation("magenta.command.jail.success.teleport.to", TagResolver.resolver(
                Placeholder.parsed("jail", jailName),
                Placeholder.parsed("player", target.name)
            )))

            val location = magenta.jailManager.getJailLocation(jailName)
            location?.let { magenta.pluginManager.callEvent(JailTeleportEvent(target, it)) }
        }

    }

    @Command("jail delete <name>")
    @Permission("magenta.jail.delete")
    @CommandDescription("This command delete jail")
    fun onJailDelete(commandSender: CommandSender, @Argument(value = "name", suggestions = "jails") jailName: String) {
        magenta.pluginManager.callEvent(JailDeleteEvent(commandSender, jailName))
    }

    @ProxiedBy("jails")
    @Command("jail list")
    @Permission("magenta.jail.list")
    @CommandDescription("This command send jail list")
    fun onJails(commandSender: CommandSender) {
        magenta.pluginManager.callEvent(JailInfoEvent(commandSender, null, InfoType.LIST))
    }

}