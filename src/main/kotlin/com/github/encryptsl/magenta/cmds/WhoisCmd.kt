package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.kmono.lib.api.commands.AnnotationFeatures
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.annotations.*
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.paper.LegacyPaperCommandManager
import com.github.encryptsl.kmono.lib.extensions.censorIpAddress
import com.github.encryptsl.kmono.lib.extensions.convertFromMillis
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.account.models.UserAccountImpl
import com.github.encryptsl.magenta.common.Permissions
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import java.net.InetAddress

@Suppress("UNUSED")
@CommandDescription("Provided by MagentaPro")
class WhoisCmd(private val magenta: Magenta) : AnnotationFeatures {

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: LegacyPaperCommandManager<CommandSender>
    ) {
        annotationParser.parse(this)
    }

    @Command("whois|who <target>")
    @Permission("magenta.whois")
    @CommandDescription("This command send information about player")
    fun onWhois(commandSender: CommandSender, @Argument(value = "target", suggestions = "offlinePlayers") target: OfflinePlayer) {
        val user = magenta.user.getUser(target.uniqueId)

        try {
            sendMessage(commandSender, "magenta.command.whois.username", user)

            sendMessage(commandSender, "magenta.command.whois.geolocation", user)

            if (target.isOnline || target.isConnected) {
                sendMessage(commandSender,"magenta.command.whois.health", user)
                sendMessage(commandSender,"magenta.command.whois.hunger", user)
            }

            sendMessage(commandSender, "magenta.command.whois.gamemode", user)

            if (user.getFlying()) {
                sendMessage(commandSender, "magenta.command.whois.flying", user)
            }

            sendMessage(commandSender, "magenta.command.whois.lastLogin", user)
            sendMessage(commandSender, "magenta.command.whois.lastSeen", user)

            sendMessage(commandSender,"magenta.command.whois.votes", user)
            if (user.isAfk()) {
                sendMessage(commandSender, "magenta.command.whois.isAfk", user)
            }
            if (user.isSocialSpy()) {
                sendMessage(commandSender, "magenta.command.whois.isSocialSpy", user)
            }
            if (user.isVanished()) {
                sendMessage(commandSender, "magenta.command.whois.isVanished", user)
            }
        } catch(e : Exception) {
            magenta.logger.severe(e.stackTraceToString())
            commandSender.sendMessage(magenta.locale.translation("magenta.exception", Placeholder.parsed("exception", e.message ?: e.localizedMessage)))
        }
    }

    private fun sendMessage(commandSender: CommandSender, message: String, user: UserAccountImpl) {
        val ip = user.getAccount().getString("ip-address").toString()
        val maxmind = magenta.geoMaxMind.getGeoMaxMing()
        val country = maxmind.country(InetAddress.getByName(ip)).country
        val timeFormat = magenta.config.getString("time-format").toString()

        val playerIP = if (commandSender.hasPermission(Permissions.WHOIS_IP)) ip else ip.censorIpAddress()

        val tag =  TagResolver.resolver(
            Placeholder.parsed("username", user.getOfflinePlayer().name.toString()),
            Placeholder.parsed("uuid", user.getOfflinePlayer().uniqueId.toString()),
            Placeholder.parsed("health", (user.getPlayer()?.health ?: 0).toString()),
            Placeholder.parsed("hunger", (user.getPlayer()?.foodLevel ?: 0).toString()),
            Placeholder.parsed("gamemode", user.getGameMode().name),
            Placeholder.parsed("flying", user.getFlying().toString()),
            Placeholder.parsed("ip_address", playerIP),
            Placeholder.parsed("last_seen", convertFromMillis(timeFormat, user.getOfflinePlayer().lastSeen)),
            Placeholder.parsed("last_login", convertFromMillis(timeFormat, user.getOfflinePlayer().lastLogin)),
            Placeholder.parsed("remaining_jail_time", user.getRemainingJailTime().toString()),
            Placeholder.parsed("is_afk", user.isAfk().toString()),
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