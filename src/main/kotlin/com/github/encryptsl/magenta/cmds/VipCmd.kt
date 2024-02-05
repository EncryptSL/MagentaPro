package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.extensions.convertInstant
import com.github.encryptsl.magenta.common.hook.luckperms.LuckPermsAPI
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class VipCmd(private val magenta: Magenta) {

    private val luckPermsAPI: LuckPermsAPI by lazy { LuckPermsAPI() }
    private val group: String = "vip"

    @Command("vip expire")
    @Permission("magenta.vip.expire")
    fun onVIPExpire(player: Player) {
        try {
            val time = luckPermsAPI.getExpireGroup(player, group)
                ?: return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.vip.error.expired")))

            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.vip.success.expire.time"), Placeholder.parsed("expire", convertInstant(time))))
        } catch (e : Exception) {
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.exception"),
                Placeholder.parsed("exception", e.message ?: e.localizedMessage)
            ))
            magenta.logger.severe(e.message ?: e.localizedMessage)
        }
    }

    @Command("vip expire <player>")
    @Permission("magenta.vip.expire.other")
    fun onVIPExpireOther(commandSender: CommandSender, @Argument(value = "player", suggestions = "offlinePlayers") target: OfflinePlayer) {
        try {
            val time = luckPermsAPI.getExpireGroup(target, group)
                ?: return commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.vip.error.expired.other"), Placeholder.parsed("player", target.name.toString())))

            commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.vip.success.expire.time.other"), TagResolver.resolver(
                Placeholder.parsed("player", target.name.toString()),
                Placeholder.parsed("expire", convertInstant(time))))
            )
        } catch (e : Exception) {
            commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.exception"),
                Placeholder.parsed("exception", e.message ?: e.localizedMessage)
            ))
            magenta.logger.severe(e.message ?: e.localizedMessage)
        }
    }
}