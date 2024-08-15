package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.kmono.lib.api.commands.AnnotationFeatures
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.annotations.*
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.paper.LegacyPaperCommandManager
import com.github.encryptsl.magenta.Magenta
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.World
import org.bukkit.command.CommandSender

@Suppress("UNUSED")
class WeatherCmd(private val magenta: Magenta) : AnnotationFeatures {

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: LegacyPaperCommandManager<CommandSender>,
    ) {
        annotationParser.parse(this)
    }

    @Command("weather clear <world> [duration]")
    @CommandDescription("This command clear thunder and raining in specific world.")
    @Permission("magenta.weather")
    fun onWeatherClear(
        commandSender: CommandSender,
        @Argument(value = "world", suggestions = "worlds") world: World,
        @Argument(value = "duration") @Default("0") duration: Int
    ) {
        world.setStorm(false)
        world.clearWeatherDuration = duration.times(20)

        commandSender.sendMessage(
            magenta.locale.translation(
                "magenta.command.weather.clear", TagResolver.resolver(
                    Placeholder.parsed("world", world.name)
                )
            )
        )
    }

    @Command("weather rain <world> [duration]")
    @CommandDescription("This command starts raining in specific world")
    @Permission("magenta.weather")
    fun onWeatherRain(
        commandSender: CommandSender,
        @Argument(value = "world", suggestions = "worlds") world: World,
        @Argument(value = "duration") @Default("0") duration: Int
    ) {
        world.setStorm(false)
        world.weatherDuration = duration.times(20)

        commandSender.sendMessage(magenta.locale.translation("magenta.command.weather.rain", TagResolver.resolver(
            Placeholder.parsed("world", world.name)
        )))
    }

    @Command("weather thunder <world> [duration]")
    @CommandDescription("This command make thunder where another world.")
    @Permission("magenta.weather")
    fun onWeatherThunderByPlayer(
        commandSender: CommandSender,
        @Argument(value = "world", suggestions = "worlds") world: World,
        @Argument(value = "duration") @Default("0") duration: Int
    ) {
        world.setStorm(true)
        world.thunderDuration = duration.times(20)

        commandSender.sendMessage(magenta.locale.translation("magenta.command.weather.thunder", TagResolver.resolver(
            Placeholder.parsed("world", world.name)
        )))
    }
}