package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.commands.AnnotationFeatures
import com.github.encryptsl.magenta.common.extensions.convertInstant
import com.github.encryptsl.magenta.common.hook.luckperms.LuckPermsAPI
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.*
import org.incendo.cloud.paper.PaperCommandManager

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class VipCmd(private val magenta: Magenta) : AnnotationFeatures {

    private val luckPermsAPI: LuckPermsAPI by lazy { LuckPermsAPI() }
    private val group: String = "vip"

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: PaperCommandManager<CommandSender>
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

            player.sendMessage(magenta.locale.translation("magenta.command.vip.success.expire.time", Placeholder.parsed("expire", convertInstant(time))))
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
                ?: return commandSender.sendMessage(magenta.locale.translation("magenta.command.vip.error.expired.other", Placeholder.parsed("player", target.name.toString())))

            commandSender.sendMessage(magenta.locale.translation("magenta.command.vip.success.expire.time.other", TagResolver.resolver(
                Placeholder.parsed("player", target.name.toString()),
                Placeholder.parsed("expire", convertInstant(time))))
            )
        } catch (e : Exception) {
            commandSender.sendMessage(magenta.locale.translation("magenta.exception",
                Placeholder.parsed("exception", e.message ?: e.localizedMessage)
            ))
            magenta.logger.severe(e.printStackTrace().toString())
        }
    }
}