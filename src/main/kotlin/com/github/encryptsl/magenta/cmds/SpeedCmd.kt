package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.kmono.lib.api.commands.AnnotationFeatures
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.annotations.*
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.paper.LegacyPaperCommandManager
import com.github.encryptsl.magenta.Magenta
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("UNUSED")
class SpeedCmd(private val magenta: Magenta) : AnnotationFeatures {

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: LegacyPaperCommandManager<CommandSender>,
    ) {
        annotationParser.parse(this)
    }

    @Command("speed <value>")
    @Permission("magenta.speed")
    @CommandDescription("This command gave other player movement or fly speed.")
    fun onSpeed(player: Player, @Argument(value = "value") @Default("1") value: Float) {
        if (player.isFlying) {
            if (magenta.config.getDouble("max-fly-speed") < value.div(10))
                return player.sendMessage(magenta.locale.translation("magenta.command.speed.error.fly.limit", TagResolver.resolver(
                    Placeholder.parsed("type", "létání"),
                    Placeholder.parsed("limit", magenta.config.getDouble("max-fly-speed").toString())
                )))

            player.flySpeed = value.div(10)
            player.sendMessage(magenta.locale.translation("magenta.command.speed.success.fly.self", Placeholder.parsed("speed", value.toString())))
        } else {
            if (magenta.config.getDouble("max-walk-speed") < value.div(10))
                return player.sendMessage(magenta.locale.translation("magenta.command.speed.error.walk.limit", TagResolver.resolver(
                    Placeholder.parsed("type", "chůze"),
                    Placeholder.parsed("limit", magenta.config.getDouble("max-walk-speed").toString())
                )))

            player.walkSpeed = value.div(10)
            player.sendMessage(magenta.locale.translation("magenta.command.speed.success.walk.self", Placeholder.parsed("speed", value.toString())))
        }
    }

    @Command("speed <value> <target>")
    @Permission("magenta.speed.other")
    @CommandDescription("This command gave other player movement or fly speed.")
    fun onSpeedOther(
        commandSender: CommandSender,
        @Argument(value = "value") @Default("1") value: Float,
        @Argument(value = "target", suggestions = "players") target: Player
    ) {
        if (target.isFlying) {
            if (magenta.config.getDouble("max-fly-speed") < value.div(10))
                return commandSender.sendMessage(magenta.locale.translation("magenta.command.speed.error.fly.limit", TagResolver.resolver(
                    Placeholder.parsed("type", "létání"),
                    Placeholder.parsed("limit", magenta.config.getDouble("max-fly-speed").toString())
                )))

            target.flySpeed = value.div(10)
            target.sendMessage(magenta.locale.translation("magenta.command.speed.success.fly.target", Placeholder.parsed("speed", value.toString())))
            commandSender.sendMessage(magenta.locale.translation("magenta.command.speed.success.fly.self.other", TagResolver.resolver(
                Placeholder.parsed("player", target.name),
                Placeholder.parsed("speed", (value / 10).toString())
            )))
        } else {
            if (magenta.config.getDouble("max-walk-speed") < value.div(10))
                return commandSender.sendMessage(magenta.locale.translation("magenta.command.speed.error.walk.limit", TagResolver.resolver(
                    Placeholder.parsed("type", "chůze"),
                    Placeholder.parsed("limit", magenta.config.getDouble("max-walk-speed").toString())
                )))

            target.walkSpeed = value.div(10)
            target.sendMessage(magenta.locale.translation("magenta.command.speed.success.walk.target", Placeholder.parsed("speed", value.toString())))
            commandSender.sendMessage(magenta.locale.translation("magenta.command.speed.success.walk.self.other", TagResolver.resolver(
                Placeholder.parsed("player", target.name),
                Placeholder.parsed("speed", (value / 10).toString())
            )))
        }
    }

}