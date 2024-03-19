package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.ores.OresMilestonesGUI
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

@Suppress("UNUSED")
class OresCmd(private val magenta: Magenta) {
    private val oresGUI: OresMilestonesGUI by lazy { OresMilestonesGUI(magenta) }
    @Command("ores")
    @Permission("magenta.ores.progress")
    @CommandDescription("This command open gui with your ores level mining progress")
    fun onOresProgress(player: Player) {
        oresGUI.openMilestonesOresGUI(player)
    }
}