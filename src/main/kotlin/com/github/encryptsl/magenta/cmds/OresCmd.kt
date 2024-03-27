package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.commands.AnnotationFeatures
import com.github.encryptsl.magenta.api.menu.modules.milestones.OresMilestonesGUI
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.AnnotationParser
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.paper.PaperCommandManager

@Suppress("UNUSED")
class OresCmd(private val magenta: Magenta) : AnnotationFeatures {
    private val oresGUI: OresMilestonesGUI by lazy { OresMilestonesGUI(magenta) }

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: PaperCommandManager<CommandSender>
    ) {
        annotationParser.parse(this)
    }

    @Command("ores")
    @Permission("magenta.ores.progress")
    @CommandDescription("This command open gui with your ores level mining progress")
    fun onOresProgress(player: Player) {
        oresGUI.openMenu(player)
    }
}