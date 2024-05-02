package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.account.models.UserAccountImpl
import com.github.encryptsl.magenta.api.commands.AnnotationFeatures
import com.github.encryptsl.magenta.common.extensions.convertFromMillis
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.incendo.cloud.annotations.*
import org.incendo.cloud.paper.PaperCommandManager
import java.net.InetAddress

@Suppress("UNUSED")
@CommandDescription("Provided by MagentaPro")
class WhoisCmd(private val magenta: Magenta) : AnnotationFeatures {

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: PaperCommandManager<CommandSender>
    ) {
        annotationParser.parse(this)
    }

    @Command("whois|who <target>")
    @Permission("magenta.whois")
    @CommandDescription("This command send information about player")
    fun onWhois(commandSender: CommandSender, @Argument(value = "target", suggestions = "offlinePlayers") target: OfflinePlayer) {
        val user = magenta.user.getUser(target.uniqueId)

        try {
            sendMessage(commandSender, magenta.locale.getMessage("magenta.command.whois.username"), target, user)

            sendMessage(commandSender, magenta.locale.getMessage("magenta.command.whois.geolocation"), target, user)

            if (target.isOnline || target.isConnected) {
                sendMessage(commandSender, magenta.locale.getMessage("magenta.command.whois.health"), target, user)
                sendMessage(commandSender, magenta.locale.getMessage("magenta.command.whois.hunger"), target, user)
            }

            sendMessage(commandSender, magenta.locale.getMessage("magenta.command.whois.gamemode"), target, user)

            if (user.getFlying()) {
                sendMessage(commandSender, magenta.locale.getMessage("magenta.command.whois.flying"), target, user)
            }

            sendMessage(commandSender, magenta.locale.getMessage("magenta.command.whois.lastLogin"), target, user)
            sendMessage(commandSender, magenta.locale.getMessage("magenta.command.whois.lastSeen"), target, user)

            sendMessage(commandSender, magenta.locale.getMessage("magenta.command.whois.votes"), target, user)
            if (magenta.afk.isAfk(target.uniqueId) || user.isAfk()) {
                sendMessage(commandSender, magenta.locale.getMessage("magenta.command.whois.isAfk"), target, user)
            }
            if (user.isSocialSpy()) {
                sendMessage(commandSender, magenta.locale.getMessage("magenta.command.whois.isSocialSpy"), target, user)
            }
            if (user.isVanished()) {
                sendMessage(commandSender, magenta.locale.getMessage("magenta.command.whois.isVanished"), target, user)
            }
        } catch(e : Exception) {
            magenta.logger.severe(e.stackTraceToString())
            commandSender.sendMessage(magenta.locale.translation("magenta.exception", Placeholder.parsed("exception", e.message ?: e.localizedMessage)))
        }
    }

    private fun sendMessage(commandSender: CommandSender, message: String, player: OfflinePlayer, user: UserAccountImpl) {
        val ip = user.getAccount().getString("ip-address").toString()
        val maxmind = magenta.database.getGeoMaxMing()
        val country = maxmind.country(InetAddress.getByName(ip)).country

        val playerIP = if (commandSender.hasPermission("magenta.whois.ip")) ip else magenta.stringUtils.censorIp(ip)

        val isAFK = magenta.afk.isAfk(player.uniqueId) || user.isAfk()

        val tag =  TagResolver.resolver(
            Placeholder.parsed("username", player.name.toString()),
            Placeholder.parsed("uuid", player.uniqueId.toString()),
            Placeholder.parsed("health", (player.player?.health ?: 0).toString()),
            Placeholder.parsed("hunger", (player.player?.foodLevel ?: 0).toString()),
            Placeholder.parsed("gamemode", user.getGameMode().name),
            Placeholder.parsed("flying", user.getFlying().toString()),
            Placeholder.parsed("ip_address", playerIP),
            Placeholder.parsed("last_seen", convertFromMillis(player.lastSeen)),
            Placeholder.parsed("last_login", convertFromMillis(player.lastLogin)),
            Placeholder.parsed("remaining_jail_time", user.getRemainingJailTime().toString()),
            Placeholder.parsed("is_afk", isAFK.toString()),
            Placeholder.parsed("is_socialspy", user.isSocialSpy().toString()),
            Placeholder.parsed("is_jailed", user.isJailed().toString()),
            Placeholder.parsed("is_vanished", user.isVanished().toString()),
            Placeholder.parsed("votes", user.getVotes().toString()),
            Placeholder.parsed("iso_code", country.isoCode),
            Placeholder.parsed("country", country.name)
        )

        commandSender.sendMessage(magenta.locale.translation(message, tag))
    }

}