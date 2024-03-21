package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.commands.AnnotationFeatures
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.command.CommandSender
import org.incendo.cloud.annotation.specifier.Greedy
import org.incendo.cloud.annotations.*
import org.incendo.cloud.paper.PaperCommandManager

@Suppress("UNUSED")
class BroadcastCmd(private val magenta: Magenta) : AnnotationFeatures {

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: PaperCommandManager<CommandSender>
    ) {
        annotationParser.parse(this)
    }

    @Command("broadcast|oznameni <message>")
    @Permission("magenta.broadcast")
    @CommandDescription("This command send broadcast message to every one.")
    fun onBroadcast(commandSender: CommandSender, @Greedy @Argument(value = "message") message: String) {
        commandSender.server.broadcast(magenta.localeConfig.translation("magenta.broadcast",
                Placeholder.parsed("message", message)
            )
        )
    }
}