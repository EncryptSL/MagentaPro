package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.ores.OresMilestonesGUI
import com.github.encryptsl.magenta.api.menu.shop.helpers.ShopUI
import com.github.encryptsl.magenta.api.scheduler.SchedulerMagenta
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class OresCmd(private val magenta: Magenta) {

    private val shopUI: ShopUI by lazy { ShopUI(magenta) }
    private val oresGUI: OresMilestonesGUI by lazy { OresMilestonesGUI(magenta) }

    @Command("ores")
    @Permission("magenta.ores.progress")
    fun onOresProgress(player: Player) {
        SchedulerMagenta.doSync(magenta) {
            oresGUI.openMilestonesOresGUI(player)
        }
    }
}