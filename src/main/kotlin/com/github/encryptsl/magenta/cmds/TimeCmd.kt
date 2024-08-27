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
class TimeCmd(private val magenta: Magenta) : AnnotationFeatures {
    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: LegacyPaperCommandManager<CommandSender>,
    ) {
        annotationParser.parse(this)
    }

    @Command("time set <number> [world]")
    @CommandDescription("This command set specific phase of day")
    @Permission("magenta.time.set")
    fun onSetTime(
        commandSender: CommandSender,
        @Argument(value = "number", description = "Phase of day") number: Int,
        @Argument(value = "world", description = "Name of world", suggestions = "worlds") world: World?
    ) {
        if (commandSender is Player) {
            val inWorld = world ?: commandSender.world
            onTimeChange(commandSender, "magenta.command.time.success.set", inWorld, number.toLong())
        } else {
            val inWorld = world ?: Bukkit.getWorlds().first()
            onTimeChange(commandSender, "magenta.command.time.success.set", inWorld, number.toLong())
        }
    }

    @Command("time set day [world]")
    @CommandDescription("This command set day in specific world or other")
    @Permission("magenta.time.set.day")
    fun setTimeDay(commandSender: CommandSender, @Argument(value = "world", description = "Name of world", suggestions = "worlds") world: World?) {
        if (commandSender is Player) {
            val inWorld = world ?: commandSender.world
            onTimeChange(commandSender, "magenta.command.time.success.set.day", inWorld, 1000)
        } else {
            setTimeDayConsole(commandSender, world)
        }
    }

    private fun setTimeDayConsole(commandSender: CommandSender, world: World?) {
        val inWorld = world ?: Bukkit.getWorlds().first()

        onTimeChange(commandSender, "magenta.command.time.success.set.day", inWorld, 1000)
    }

    @Command("time set midnight [world]")
    @CommandDescription("This command set midnight in specific world or other")
    @Permission("magenta.time.set.midnight")
    fun setTimeMidNightPlayer(commandSender: CommandSender, @Argument(value = "world", description = "Name of world", suggestions = "worlds") world: World?) {
        if (commandSender is Player) {
            val inWorld = world ?: commandSender.world
            onTimeChange(commandSender, "magenta.command.time.success.set.midnight", inWorld, 18000)
        } else {
            setTimeMidNightConsole(commandSender, world)
        }
    }

    private fun setTimeMidNightConsole(commandSender: CommandSender, world: World?) {
        val inWorld = world ?: Bukkit.getWorlds().first()
        onTimeChange(commandSender, "magenta.command.time.success.set.midnight", inWorld, 18000)
    }

    @Command("time set night [world]")
    @CommandDescription("This command set night in specific world or other")
    @Permission("magenta.time.set.night")
    fun setTimeNightPlayer(commandSender: CommandSender, @Argument(value = "world", description = "Name of world", suggestions = "worlds") world: World?) {
        if (commandSender is Player) {
            val inWorld = world ?: commandSender.world
            onTimeChange(commandSender, "magenta.command.time.success.set.night", inWorld, 13000)
        } else {
            setTimeMidNightConsole(commandSender, world)
        }
    }

    private fun setTimeNightConsole(commandSender: CommandSender, world: World?) {
        val inWorld = world ?: Bukkit.getWorlds().first()

        onTimeChange(commandSender, "magenta.command.time.success.set.night", inWorld, 13000)
    }

    private fun onTimeChange(commandSender: CommandSender, translation: String, world: World, time: Long) {
        world.time = time
        commandSender.sendMessage(magenta.locale.translation(translation, TagResolver.resolver(
            Placeholder.parsed("world", world.name),
            Placeholder.parsed("day", time.toString())
        )))
    }
}