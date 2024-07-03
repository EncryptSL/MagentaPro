package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.kmono.lib.api.commands.AnnotationFeatures
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.annotations.AnnotationParser
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.annotations.Command
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.annotations.CommandDescription
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.annotations.Permission
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.paper.LegacyPaperCommandManager
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.modules.milestones.OresMilestonesGUI
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("UNUSED")
class OresCmd(private val magenta: Magenta) : AnnotationFeatures {
    private val oresGUI: OresMilestonesGUI by lazy { OresMilestonesGUI(magenta) }

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: LegacyPaperCommandManager<CommandSender>
    ) {
        annotationParser.parse(this)
    }

    @Command("ores")
    @Permission("magenta.ores.progress")
    @CommandDescription("This command open gui with your ores level mining progress")
    fun onOresProgress(player: Player) {
        oresGUI.open(player)
    }
}