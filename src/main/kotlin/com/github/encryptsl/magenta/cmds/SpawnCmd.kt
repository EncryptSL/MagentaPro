package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.kmono.lib.api.commands.AnnotationFeatures
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.annotations.*
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.paper.LegacyPaperCommandManager
import com.github.encryptsl.magenta.Magenta
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SpawnCmd(private val magenta: Magenta) : AnnotationFeatures {
    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: LegacyPaperCommandManager<CommandSender>,
    ) {
        annotationParser.parse(this)
    }

    @Command("spawn")
    @Permission("magenta.spawn")
    @CommandDescription("Teleport yourself to spawn !")
    fun onSpawn(
        player: Player,
    ) {
        if (!magenta.spawnConfig.getConfig().contains("spawn")) {
            return player.sendMessage(magenta.locale.translation("magenta.command.spawn.error.not.exist"))
        }

        magenta.spawnConfig.getConfig().getLocation("spawn")?.let { player.teleportAsync(it) }
        player.sendMessage(magenta.locale.translation("magenta.command.spawn.success.teleport.self"))
    }

    @Command("spawn <target>")
    @Permission("magenta.spawn.other")
    @CommandDescription("Teleport other player to spawn")
    fun onSpawnOther(
        commandSender: CommandSender,
        @Argument(value = "target", suggestions = "players") target: Player,
    ) {
        if (!magenta.spawnConfig.getConfig().contains("spawn")) {
            return commandSender.sendMessage(magenta.locale.translation("magenta.command.spawn.error.not.exist"))
        }

        magenta.spawnConfig.getConfig().getLocation("spawn")?.let { target.teleportAsync(it) }
        target.sendMessage(magenta.locale.translation("magenta.command.spawn.success.teleport.target"))
        commandSender.sendMessage(magenta.locale.translation("magenta.command.spawn.success.teleport.self.other",
            Placeholder.parsed("target", target.name)))
    }

    @Command("setspawn [location]")
    @Permission("magenta.setspawn")
    @CommandDescription("This command create spawn where you stand or other coordination !")
    fun onSetSpawn(player: Player, @Argument("location") location: Location?) {
        if (location == null) {
            magenta.spawnConfig.set("spawn", player.location)
        } else {
            magenta.spawnConfig.set("spawn", location)
        }

        player.sendMessage(magenta.locale.translation("magenta.command.spawn.success.setspawn", Placeholder.parsed("location",
            (location ?: player.location).toString()
        )))
    }
}