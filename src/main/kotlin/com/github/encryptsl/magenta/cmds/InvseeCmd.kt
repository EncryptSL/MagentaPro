package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.github.encryptsl.magenta.Magenta
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class InvseeCmd(private val magenta: Magenta) {
    @CommandMethod("invsee <target>")
    @CommandPermission("magenta.invsee")
    fun onInvseePlayer(player: Player, @Argument(value = "target", suggestions = "players") target: Player) {
        magenta.schedulerMagenta.doSync(magenta) {
            player.openInventory(target.inventory)
        }
    }
}