package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotation.specifier.Greedy
import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.Command
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.Permission
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class HelpOpCmd(private val magenta: Magenta) {

    @Command("helpop <message>")
    @Permission("magenta.helpop")
    fun onHelpOp(player: Player, @Argument(value = "message") @Greedy message: String) {
        val component = ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.helpop.staffchat"), TagResolver.resolver(
            Placeholder.parsed("player", player.name),
            Placeholder.parsed("message", message)))

        Bukkit.broadcast(component, "magenta.helpop.staff.chat")
        player.sendMessage(component)
    }

}