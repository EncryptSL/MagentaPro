package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.kmono.lib.api.commands.AnnotationFeatures
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.annotations.*
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.paper.LegacyPaperCommandManager
import com.github.encryptsl.magenta.Magenta
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("UNUSED")
class WeatherCmd(private val magenta: Magenta) : AnnotationFeatures {

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: LegacyPaperCommandManager<CommandSender>,
    ) {
        annotationParser.parse(this)
    }

    @Command("weather clear [world] [duration]")
    @CommandDescription("This command clear thunder and raining in specific world.")
    @Permission("magenta.weather")
    fun onWeatherClearPlayer(
        commandSender: CommandSender,
        @Argument(value = "world", suggestions = "worlds") world: World?,
        @Argument(value = "duration") @Default("0") duration: Int
    ) {
        if (commandSender is Player) {
            val inWorld = world ?: commandSender.world

            inWorld.setStorm(false)
            inWorld.clearWeatherDuration = duration.times(20)
            changeWeather(commandSender, "magenta.command.weather.clear", inWorld)
        } else {
            val inWorld = world ?: Bukkit.getWorlds().first()

            inWorld.setStorm(false)
            inWorld.clearWeatherDuration = duration.times(20)
            changeWeather(commandSender, "magenta.command.weather.clear", inWorld)
        }
    }

    @Command("weather rain [world] [duration]")
    @CommandDescription("This command starts raining in specific world")
    @Permission("magenta.weather")
    fun onWeatherRainPlayer(
        commandSender: CommandSender,
        @Argument(value = "world", suggestions = "worlds") world: World?,
        @Argument(value = "duration") @Default("0") duration: Int
    ) {
        if (commandSender is Player) {
            val inWorld = world ?: commandSender.world

            inWorld.setStorm(false)
            inWorld.weatherDuration = duration.times(20)

            changeWeather(commandSender, "magenta.command.weather.rain", inWorld)
        } else {
            val inWorld = world ?: Bukkit.getWorlds().first()

            inWorld.setStorm(false)
            inWorld.weatherDuration = duration.times(20)

            changeWeather(commandSender, "magenta.command.weather.rain", inWorld)
        }
    }


    @Command("weather thunder [world] [duration]")
    @CommandDescription("This command make thunder where another world.")
    @Permission("magenta.weather")
    fun onWeatherThunder(
        commandSender: CommandSender,
        @Argument(value = "world", suggestions = "worlds") world: World?,
        @Argument(value = "duration") @Default("0") duration: Int
    ) {
        if (commandSender is Player) {
            val inWorld = world ?: commandSender.world
            inWorld.setStorm(true)
            inWorld.thunderDuration = duration.times(20)
            changeWeather(commandSender, "magenta.command.weather.thunder", world ?: commandSender.world)
        } else {
            val inWorld = world ?: Bukkit.getWorlds().first()
            inWorld.setStorm(true)
            inWorld.thunderDuration = duration.times(20)
            changeWeather(commandSender, "magenta.command.weather.thunder", inWorld)
        }
    }

    private fun changeWeather(commandSender: CommandSender, translation: String, world: World) {
        commandSender.sendMessage(magenta.locale.translation(translation, TagResolver.resolver(
            Placeholder.parsed("world", world.name)
        )))
    }
}