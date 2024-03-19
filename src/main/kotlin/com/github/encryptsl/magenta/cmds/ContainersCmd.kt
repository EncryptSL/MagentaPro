package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

@Suppress("UNUSED")
class ContainersCmd(private val magenta: Magenta) {

    @Command("anvil")
    @Permission("magenta.anvil")
    @CommandDescription("This command open virtual anvil")
    fun onAnvil(player: Player) {
        magenta.paperContainerProvider.openAnvil(player)
    }

    @Command("grindstone|gstone")
    @Permission("magenta.grindstone")
    @CommandDescription("This command open virtual grindstone")
    fun onGrindstone(player: Player) {
        magenta.paperContainerProvider.openGrindStone(player)
    }

    @Command("loom")
    @Permission("magenta.loom")
    @CommandDescription("This command open virtual loom")
    fun onLoom(player: Player) {
        magenta.paperContainerProvider.openLoom(player)
    }

    @Command("stonecutter|stonec")
    @Permission("magenta.stonecutter")
    @CommandDescription("This command open virtual stone cutter")
    fun onStoneCutter(player: Player) {
        magenta.paperContainerProvider.openStonecutter(player)
    }

    @Command("smithingtable|smithing")
    @Permission("magenta.smithingtable")
    @CommandDescription("This command open virtual smithing table")
    fun onSmithingTable(player: Player) {
        magenta.paperContainerProvider.openSmithingTable(player)
    }

    @Command("workbench|crafting")
    @Permission("magenta.workbench")
    @CommandDescription("This command open virtual crafting table")
    fun onWorkBench(player: Player) {
        magenta.paperContainerProvider.openWorkBench(player)
    }

}