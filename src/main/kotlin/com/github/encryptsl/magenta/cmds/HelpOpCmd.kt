package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.commands.AnnotationFeatures
import com.github.encryptsl.magenta.common.hook.luckperms.LuckPermsAPI
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotation.specifier.Greedy
import org.incendo.cloud.annotations.*
import org.incendo.cloud.paper.PaperCommandManager

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class HelpOpCmd(private val magenta: Magenta) : AnnotationFeatures {

    private val luckPermsHook: LuckPermsAPI by lazy { LuckPermsAPI() }

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: PaperCommandManager<CommandSender>
    ) {
        annotationParser.parse(this)
    }

    @Command("helpop <message>")
    @Permission("magenta.helpop")
    @CommandDescription("This command send help message to online administrators")
    fun onHelpOp(player: Player, @Argument(value = "message") @Greedy message: String) {
        val component = chat(player, null, message, "magenta.command.helpop.chat")

        Bukkit.broadcast(component, "magenta.helpop.staff.chat")
        player.sendMessage(component)
    }

    @Command("helpanswer|hanswer <target> <message>")
    @Permission("magenta.helpop.staff.chat")
    @CommandDescription("This command send answer to player")
    fun onHelpOpRespond(
        commandSender: CommandSender,
        @Argument(value = "target", suggestions = "players") target: Player,
        @Argument(value = "message") @Greedy message: String
    ) {
        if (commandSender is Player) {
            if (commandSender.uniqueId == target.uniqueId) return
            return sendChannelMessage(commandSender, target, message)
        }

        sendChannelMessage(commandSender, target, message)
    }

    private fun sendChannelMessage(commandSender: CommandSender, target: Player, message: String) {
        val component = chat(commandSender, target, message, "magenta.command.helpop.answer.chat")
        Bukkit.broadcast(component, "magenta.helpop.staff.chat")
        target.sendMessage(component)
    }

    private fun chat(from: CommandSender, to: Player?, message: String, locale: String): Component {
        val group = if (from is Player) magenta.stringUtils.colorize(luckPermsHook.getPrefix(from)) else ""

       return magenta.localeConfig.translation(locale, TagResolver.resolver(
            Placeholder.component("group", Component.text(group)),
            Placeholder.parsed("from", from.name),
            Placeholder.parsed("to", to?.name ?: ""),
            Placeholder.parsed("message", message)))
    }

}