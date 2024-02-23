package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.account.UserAccount
import com.github.encryptsl.magenta.common.database.DatabaseConnector
import com.github.encryptsl.magenta.common.extensions.convertInstant
import com.github.encryptsl.magenta.common.utils.ModernText
import com.maxmind.geoip2.DatabaseReader
import com.maxmind.geoip2.record.Country
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission
import java.net.InetAddress
import java.time.Instant

@Suppress("UNUSED")
@CommandDescription("Provided by MagentaPro")
class WhoisCmd(private val magenta: Magenta) {

    private val maxmind: DatabaseReader = DatabaseConnector(magenta).getGeoMaxMing()

    @Command("whois|who <target>")
    @Permission("magenta.whois")
    fun onWhois(commandSender: CommandSender, @Argument(value = "target", suggestions = "offlinePlayers") target: OfflinePlayer) {
        val user = magenta.user.getUser(target.uniqueId)
        val ip = user.getAccount().getString("ip-address").toString()

        try {
            val country = maxmind.country(InetAddress.getByName(ip)).country
            sendMessage(commandSender, magenta.localeConfig.getMessage("magenta.command.whois.username"), user, ip, target, country)
            if (commandSender.hasPermission("magenta.whois.ip")) {
                sendMessage(commandSender, magenta.localeConfig.getMessage("magenta.command.whois.geolocation"), user, ip, target, country)
            } else {
                sendMessage(commandSender, magenta.localeConfig.getMessage("magenta.command.whois.geolocation"), user, magenta.stringUtils.censorIp(
                    ip
                ), target, country)
            }

            if (target.isOnline || target.isConnected) {
                sendMessage(commandSender, magenta.localeConfig.getMessage("magenta.command.whois.health"), user, ip, target, country)
                sendMessage(commandSender, magenta.localeConfig.getMessage("magenta.command.whois.hunger"), user, ip, target, country)
            }

            sendMessage(commandSender, magenta.localeConfig.getMessage("magenta.command.whois.gamemode"), user, ip, target, country)

            sendMessage(commandSender, magenta.localeConfig.getMessage("magenta.command.whois.lastSeen"), user, ip, target, country)
            sendMessage(commandSender, magenta.localeConfig.getMessage("magenta.command.whois.lastLogin"), user, ip, target, country)

            sendMessage(commandSender, magenta.localeConfig.getMessage("magenta.command.whois.votes"), user, ip, target, country)
            if (magenta.afk.isAfk(target.uniqueId) || user.isAfk()) {
                sendMessage(commandSender, magenta.localeConfig.getMessage("magenta.command.whois.isAfk"), user, ip, target, country)
            }
            if (user.isSocialSpy()) {
                sendMessage(commandSender, magenta.localeConfig.getMessage("magenta.command.whois.isSocialSpy"), user, ip, target, country)
            }
            if (user.isVanished()) {
                sendMessage(commandSender, magenta.localeConfig.getMessage("magenta.command.whois.isVanished"), user, ip, target, country)
            }
        } catch(e : Exception) {
            magenta.logger.severe(e.stackTraceToString())
            commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.exception"), Placeholder.parsed("exception", e.message ?: e.localizedMessage)))
        }
    }

    private fun sendMessage(commandSender: CommandSender, message: String, user: UserAccount, ip: String, player: OfflinePlayer, country: Country) {
        val tag =  TagResolver.resolver(
            Placeholder.parsed("username", player.name.toString()),
            Placeholder.parsed("uuid", player.uniqueId.toString()),
            Placeholder.parsed("health", (player.player?.health ?: 0).toString()),
            Placeholder.parsed("hunger", (player.player?.foodLevel ?: 0).toString()),
            Placeholder.parsed("gamemode", user.getGameMode().name),
            Placeholder.parsed("flying", user.getFlying().toString()),
            Placeholder.parsed("ip_address", ip),
            Placeholder.parsed("last_seen", convertInstant(Instant.ofEpochMilli(player.lastSeen))),
            Placeholder.parsed("last_login", convertInstant(Instant.ofEpochMilli(player.lastLogin))),
            Placeholder.parsed("remaining_jail_time", user.getRemainingJailTime().toString()),
            Placeholder.parsed("is_afk", ((user.isAfk() || magenta.afk.isAfk(player.uniqueId)).toString())),
            Placeholder.parsed("is_socialspy", user.isSocialSpy().toString()),
            Placeholder.parsed("is_jailed", user.isJailed().toString()),
            Placeholder.parsed("is_vanished", user.isVanished().toString()),
            Placeholder.parsed("votes", user.getVotes().toString()),
            Placeholder.parsed("iso_code", country.isoCode),
            Placeholder.parsed("country", country.name)
        )

        commandSender.sendMessage(ModernText.miniModernText(message, tag))
    }

}