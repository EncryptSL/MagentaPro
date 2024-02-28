package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotation.specifier.Greedy
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class HelpOpCmd(private val magenta: Magenta) {

    @Command("helpop <message>")
    @Permission("magenta.helpop")
    fun onHelpOp(player: Player, @Argument(value = "message") @Greedy message: String) {
        val component = chat(player, null, message, "magenta.command.helpop.chat")

        Bukkit.broadcast(component, "magenta.helpop.staff.chat")
        player.sendMessage(component)
    }

    @Command("helpanswer|hanswer <target> <message>")
    @Permission("magenta.helpop.staff.chat")
    fun onHelpOpRespond(
        commandSender: CommandSender,
        @Argument(value = "target", suggestions = "players")
        target: Player,
        @Argument(value = "message") @Greedy message: String
    ) {
        val component = chat(commandSender, target, message, "magenta.command.helpop.answer.chat")
        Bukkit.broadcast(component, "magenta.helpop.staff.chat")
        target.sendMessage(component)
    }

    private fun chat(from: CommandSender, to: Player?, message: String, locale: String): Component {
       return ModernText.miniModernText(magenta.localeConfig.getMessage(locale), TagResolver.resolver(
            Placeholder.parsed("from", from.name),
            Placeholder.parsed("to", to?.name ?: ""),
            Placeholder.parsed("message", message)))
    }

}