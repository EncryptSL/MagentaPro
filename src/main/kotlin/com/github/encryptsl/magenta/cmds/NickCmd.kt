package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class NickCmd(private val magenta: Magenta) {

    @CommandMethod("nick <nickname>")
    @CommandPermission("magenta.nick")
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

        player.displayName(ModernText.miniModernText(nickName))
        player.playerListName(ModernText.miniModernText(nickName))
        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.nick.success.changed"),
            Placeholder.parsed("nickname", nickName)
        ))
    }

    @CommandMethod("nick <nickname> <player>")
    @CommandPermission("magenta.nick.other")
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
        target.displayName(ModernText.miniModernText(nickName))
        target.playerListName(ModernText.miniModernText(nickName))
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.nick.success.changed.to"), TagResolver.resolver(
            Placeholder.parsed("nickname", nickName),
            Placeholder.parsed("player", target.name)
        )))
    }

    @CommandMethod("unnick")
    @CommandPermission("magenta.nick.other")
    fun onUnNick(player: Player) {
        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.nick.success.changed.back"), Placeholder.parsed("nickname", player.name)))
        player.displayName(ModernText.miniModernText(player.name))
        player.playerListName(player.name())
    }

    @CommandMethod("unnick <player>")
    @CommandPermission("magenta.unnick.other")
    fun onUnNickOther(commandSender: CommandSender, @Argument(value = "player", suggestions = "players") target: Player) {
        target.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.nick.success.changed.back"), Placeholder.parsed("nickname", target.name)))
        target.displayName(target.name())
        target.playerListName(target.name())
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.nick.success.changed.back.to"), TagResolver.resolver(
            Placeholder.parsed("nickname", target.name),
            Placeholder.parsed("player", target.name)
        )))
    }

}