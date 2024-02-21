package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.account.UserAccount
import com.github.encryptsl.magenta.common.database.DatabaseConnector
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

@Suppress("UNUSED")
@CommandDescription("Provided by MagentaPro")
class WhoisCmd(private val magenta: Magenta) {

    private val maxmind: DatabaseReader = DatabaseConnector(magenta).getGeoMaxMing()

    @Command("whois|who <target>")
    @Permission("magenta.whois")
    fun onWhois(commandSender: CommandSender, @Argument(value = "target", suggestions = "offlinePlayers") target: OfflinePlayer) {
        val user = magenta.user.getUser(target.uniqueId)
        val ip = user.getAccount().getString("ip-address") ?: "N/A"

        val country = maxmind.country(InetAddress.getByAddress(ip.toByteArray())).country

        sendMessage(commandSender, magenta.localeConfig.getMessage("magenta.command.whois.username"), user, ip, target, country)

        if (commandSender.hasPermission("magenta.whois.ip")) {
            sendMessage(commandSender, magenta.localeConfig.getMessage("magenta.command.whois.geolocation"), user, ip, target, country)
        } else {
            sendMessage(commandSender, magenta.localeConfig.getMessage("magenta.command.whois.geolocation"), user, magenta.stringUtils.censorIp(ip), target, country)
        }

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
    }

    private fun sendMessage(commandSender: CommandSender, message: String, user: UserAccount, ip: String, player: OfflinePlayer, country: Country) {
        val tag =  TagResolver.resolver(
            Placeholder.parsed("username", player.name.toString()),
            Placeholder.parsed("ip_address", ip),
            Placeholder.parsed("lastSeen", player.lastSeen.toString()),
            Placeholder.parsed("lastLogin", player.lastLogin.toString()),
            Placeholder.parsed("remainingJailTime", user.getRemainingJailTime().toString()),
            Placeholder.parsed("isAfk", ((user.isAfk() || magenta.afk.isAfk(player.uniqueId)).toString())),
            Placeholder.parsed("isSocialSpy", user.isSocialSpy().toString()),
            Placeholder.parsed("isJailed", user.isJailed().toString()),
            Placeholder.parsed("isVanished", user.isVanished().toString()),
            Placeholder.parsed("votes", user.getVotes().toString()),
            Placeholder.parsed("iso_code", country.isoCode),
            Placeholder.parsed("country", country.name)
        )

        commandSender.sendMessage(ModernText.miniModernText(message, tag))
    }

}