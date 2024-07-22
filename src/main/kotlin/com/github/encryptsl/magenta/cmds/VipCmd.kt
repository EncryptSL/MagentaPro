package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.kmono.lib.api.commands.AnnotationFeatures
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.annotations.*
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.paper.LegacyPaperCommandManager
import com.github.encryptsl.kmono.lib.extensions.convertInstant
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.hook.luckperms.LuckPermsAPI
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toKotlinInstant
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.apache.commons.lang3.time.DateFormatUtils
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class VipCmd(private val magenta: Magenta) : AnnotationFeatures {

    private val luckPermsAPI: LuckPermsAPI by lazy { LuckPermsAPI() }
    private val group: String = "vip"
    private val dateTimeFormat = "dd.MM hh:mm yyyy"

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: LegacyPaperCommandManager<CommandSender>
    ) {
        annotationParser.parse(this)
    }

    @Command("vipexpire")
    @Permission("magenta.vip.expire")
    @CommandDescription("This command send information about your vip expiration")
    fun onVIPExpire(player: Player) {
        try {
            val time = luckPermsAPI.getExpireGroup(player, group)
                ?: return player.sendMessage(magenta.locale.translation("magenta.command.vip.error.expired"))

            player.sendMessage(magenta.locale.translation("magenta.command.vip.success.expire.time", TagResolver.resolver(
                Placeholder.parsed("expire", convertInstant(time, dateTimeFormat)),
                Placeholder.parsed("days", Clock.System.now().daysUntil(time.toKotlinInstant(), TimeZone.currentSystemDefault()).toString())
            )))
        } catch (e : Exception) {
            player.sendMessage(magenta.locale.translation("magenta.exception",
                Placeholder.parsed("exception", e.message ?: e.localizedMessage)
            ))
            magenta.logger.severe(e.message ?: e.localizedMessage)
        }
    }

    @Command("vipexpire <player>")
    @Permission("magenta.vip.expire.other")
    @CommandDescription("This command send information about other player vip expiration")
    fun onVIPExpireOther(commandSender: CommandSender, @Argument(value = "player", suggestions = "offlinePlayers") target: OfflinePlayer) {
        try {
            val time = luckPermsAPI.getExpireGroup(target, group)
                ?: return commandSender.sendMessage(
                    magenta.locale.translation("magenta.command.vip.error.expired.other",
                        Placeholder.parsed("player", target.name.toString())
                    ))

            commandSender.sendMessage(magenta.locale.translation("magenta.command.vip.success.expire.time.other", TagResolver.resolver(
                Placeholder.parsed("player", target.name.toString()),
                Placeholder.parsed("expire", DateFormatUtils.format(time.toEpochMilli(), dateTimeFormat))))
            )
        } catch (e : Exception) {
            commandSender.sendMessage(magenta.locale.translation("magenta.exception",
                Placeholder.parsed("exception", e.message ?: e.localizedMessage)
            ))
            magenta.logger.severe(e.printStackTrace().toString())
        }
    }
}