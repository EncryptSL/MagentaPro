package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.kmono.lib.api.commands.AnnotationFeatures
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.annotation.specifier.Greedy
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.annotations.*
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.paper.LegacyPaperCommandManager
import com.github.encryptsl.magenta.Magenta
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.title.TitlePart
import org.bukkit.command.CommandSender

@Suppress("UNUSED")
class BroadcastCmd(private val magenta: Magenta) : AnnotationFeatures {

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: LegacyPaperCommandManager<CommandSender>
    ) {
        annotationParser.parse(this)
    }

    @ProxiedBy("bc")
    @Command("broadcast|oznameni <message>")
    @Permission("magenta.broadcast")
    @CommandDescription("This command send broadcast message to every one.")
    fun onBroadcast(commandSender: CommandSender, @Greedy @Argument(value = "message") message: String) {
        commandSender.server.broadcast(magenta.locale.translation("magenta.broadcast",
                Placeholder.parsed("message", message)
            )
        )
    }

    @ProxiedBy("bctitle")
    @Command("broadcast title <message>")
    @Permission("magenta.broadcast")
    @CommandDescription("This command send title broadcast message to every one.")
    fun onBroadcastTitle(
        commandSender: CommandSender,
        @Greedy @Argument(value = "message") message: String,
    ) {
        commandSender.server.sendTitlePart<Component>(TitlePart.TITLE, ModernText.miniModernText(magenta.config.getString("prefix").toString()))
        commandSender.server.sendTitlePart<Component>(TitlePart.SUBTITLE, ModernText.miniModernText(message))
    }
}