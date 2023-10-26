package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.github.encryptsl.magenta.Magenta
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class ContainersCmd(private val magenta: Magenta) {

    @CommandMethod("anvil")
    @CommandPermission("magenta.anvil")
    fun onAnvil(player: Player) {
        magenta.schedulerMagenta.doSync(magenta) {
            magenta.paperContainerProvider.openAnvil(player)
        }
    }

    @CommandMethod("grindstone|gstone")
    @CommandPermission("magenta.grindstone")
    fun onGrindstone(player: Player) {
        magenta.schedulerMagenta.doSync(magenta) {
            magenta.paperContainerProvider.openGrindStone(player)
        }
    }

    @CommandMethod("loom")
    @CommandPermission("magenta.loom")
    fun onLoom(player: Player) {
        magenta.schedulerMagenta.doSync(magenta) {
            magenta.paperContainerProvider.openLoom(player)
        }
    }

    @CommandMethod("stonecutter|stonec")
    @CommandPermission("magenta.stonecutter")
    fun onStoneCutter(player: Player) {
        magenta.schedulerMagenta.doSync(magenta) {
            magenta.paperContainerProvider.openStonecutter(player)
        }
    }

    @CommandMethod("smithingtable|smithing")
    @CommandPermission("magenta.smithingtable")
    fun onSmithingTable(player: Player) {
        magenta.schedulerMagenta.doSync(magenta) {
            magenta.paperContainerProvider.openSmithingTable(player)
        }
    }

    @CommandMethod("workbench|crafting")
    @CommandPermission("magenta.workbench")
    fun onWorkBench(player: Player) {
        magenta.schedulerMagenta.doSync(magenta) {
            magenta.paperContainerProvider.openWorkBench(player)
        }
    }

}