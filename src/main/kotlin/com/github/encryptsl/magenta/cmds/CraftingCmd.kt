package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.github.encryptsl.magenta.Magenta
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class CraftingCmd(private val magenta: Magenta) {
    @CommandMethod("crafting")
    @CommandPermission("magenta.crafting")
    fun onCrafting(player: Player) {
        magenta.schedulerMagenta.doSync(magenta){
            player.openWorkbench(null, true)
        }
    }
}