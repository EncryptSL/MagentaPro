package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.commands.AnnotationFeatures
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.*
import org.incendo.cloud.paper.PaperCommandManager

@Suppress("UNUSED")
class LightningCmd(private val magenta: Magenta) : AnnotationFeatures {

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: PaperCommandManager<CommandSender>
    ) {
        annotationParser.parse(this)
    }

    @Command("lightning|thor <player> [damage]")
    @Permission("magenta.lightning")
    @CommandDescription("This command create lightning effect with dmg or without to player")
    fun onLightning(commandSender: CommandSender, @Argument(value = "player", suggestions = "players") target: Player, @Default(value = "false") @Argument(value = "damage") damage: Boolean) {
        if (damage) target.world.strikeLightning(target.location) else target.world.strikeLightningEffect(target.location)

        commandSender.sendMessage(magenta.locale.translation("magenta.command.lightning.success.to",
            Placeholder.parsed("player", target.name)
        ))
    }
}