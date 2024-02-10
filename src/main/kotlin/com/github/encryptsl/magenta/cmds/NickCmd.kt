package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.CommandHelper
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class NickCmd(private val magenta: Magenta) {

    private val commandHelper = CommandHelper(magenta)

    @Command("nick <nickname>")
    @Permission("magenta.nick")
    fun onNick(player: Player, @Argument(value = "nickname") nickName: String) {
        if (magenta.stringUtils.isNickBlacklisted(nickName) && !player.hasPermission("magenta.nick.blacklist.bypass"))
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.nick.error.blacklisted"),
                Placeholder.parsed("nickname", magenta.stringUtils.replaceNickName(nickName)))
            )
        if (!magenta.stringUtils.isNickInAllowedLength(nickName))
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.nick.error.length"), TagResolver.resolver(
                Placeholder.parsed("nickname", magenta.stringUtils.replaceNickName(nickName)),
                Placeholder.parsed("length", magenta.stringUtils.replaceNickName(nickName).length.toString()),
                Placeholder.parsed("maxlength", magenta.config.getInt("max-nick-length").toString())
            )))

        commandHelper.changeDisplayName(player, nickName)
        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.nick.success.changed"),
            Placeholder.parsed("nickname", nickName)
        ))
    }

    @Command("nick <nickname> <player>")
    @Permission("magenta.nick.other")
    fun onNickOther(commandSender: CommandSender, @Argument(value = "nickname") nickName: String, @Argument(value = "player", suggestions = "players") target: Player) {
        if (magenta.stringUtils.isNickBlacklisted(nickName) && !target.hasPermission("magenta.nick.blacklist.bypass"))
            return commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.nick.error.blacklisted"),
                Placeholder.parsed("nickname", magenta.stringUtils.replaceNickName(nickName))
            ))

        if (!magenta.stringUtils.isNickInAllowedLength(nickName))
            return commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.nick.error.length"), TagResolver.resolver(
                Placeholder.parsed("nickname", magenta.stringUtils.replaceNickName(nickName)),
                Placeholder.parsed("length", magenta.stringUtils.replaceNickName(nickName).length.toString()),
                Placeholder.parsed("maxlength", magenta.config.getInt("max-nick-length").toString())
            )))

        target.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.nick.success.changed"), Placeholder.parsed("nickname", nickName)))

        commandHelper.changeDisplayName(target, nickName)
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.nick.success.changed.to"), TagResolver.resolver(
            Placeholder.parsed("nickname", nickName),
            Placeholder.parsed("player", target.name)
        )))
    }

    @Command("unnick")
    @Permission("magenta.nick.other")
    fun onUnNick(player: Player) {
        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.nick.success.changed.back"), Placeholder.parsed("nickname", player.name)))
        commandHelper.changeDisplayName(player, player.name)
    }

    @Command("unnick <player>")
    @Permission("magenta.unnick.other")
    fun onUnNickOther(commandSender: CommandSender, @Argument(value = "player", suggestions = "players") target: Player) {
        target.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.nick.success.changed.back"), Placeholder.parsed("nickname", target.name)))
        commandHelper.changeDisplayName(target, target.name)
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.nick.success.changed.back.to"), TagResolver.resolver(
            Placeholder.parsed("nickname", target.name),
            Placeholder.parsed("player", target.name)
        )))
    }

}