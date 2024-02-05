package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.scheduler.SchedulerMagenta
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class ContainersCmd(private val magenta: Magenta) {

    @Command("anvil")
    @Permission("magenta.anvil")
    fun onAnvil(player: Player) {
        SchedulerMagenta.doSync(magenta) {
            magenta.paperContainerProvider.openAnvil(player)
        }
    }

    @Command("grindstone|gstone")
    @Permission("magenta.grindstone")
    fun onGrindstone(player: Player) {
        SchedulerMagenta.doSync(magenta) {
            magenta.paperContainerProvider.openGrindStone(player)
        }
    }

    @Command("loom")
    @Permission("magenta.loom")
    fun onLoom(player: Player) {
        SchedulerMagenta.doSync(magenta) {
            magenta.paperContainerProvider.openLoom(player)
        }
    }

    @Command("stonecutter|stonec")
    @Permission("magenta.stonecutter")
    fun onStoneCutter(player: Player) {
        SchedulerMagenta.doSync(magenta) {
            magenta.paperContainerProvider.openStonecutter(player)
        }
    }

    @Command("smithingtable|smithing")
    @Permission("magenta.smithingtable")
    fun onSmithingTable(player: Player) {
        SchedulerMagenta.doSync(magenta) {
            magenta.paperContainerProvider.openSmithingTable(player)
        }
    }

    @Command("workbench|crafting")
    @Permission("magenta.workbench")
    fun onWorkBench(player: Player) {
        SchedulerMagenta.doSync(magenta) {
            magenta.paperContainerProvider.openWorkBench(player)
        }
    }

}