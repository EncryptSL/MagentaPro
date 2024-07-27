package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.kmono.lib.api.commands.AnnotationFeatures
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.annotations.*
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.paper.LegacyPaperCommandManager
import com.github.encryptsl.magenta.Magenta
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.entity.CreatureSpawnEvent

@Suppress("UNUSED")
class SpawnMobCmd(private val magenta: Magenta) : AnnotationFeatures {
    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: LegacyPaperCommandManager<CommandSender>,
    ) {
        annotationParser.parse(this)
    }

    @Command("spawnmob <mob> [number]")
    @Permission("magenta.spawnmob")
    @CommandDescription("This command spawn mob where player looking.")
    fun onSpawnMob(
        player: Player,
        @Argument(value = "mob", suggestions = "mobs") entity: EntityType,
        @Argument(value = "number") @Default("1") number: Int
    ) {
        val range = number..magenta.config.getInt("spawnmob-limit")
        for (i in range) {
            player.world.spawnEntity(player.eyeLocation, entity, CreatureSpawnEvent.SpawnReason.COMMAND)
        }
    }
}