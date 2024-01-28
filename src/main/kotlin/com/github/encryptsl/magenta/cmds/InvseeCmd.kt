package com.github.encryptsl.magenta.cmds

import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission
import com.github.encryptsl.magenta.Magenta
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class InvseeCmd(private val magenta: Magenta) {
    @Command("invsee <target>")
    @Permission("magenta.invsee")
    fun onInvseePlayer(player: Player, @Argument(value = "target", suggestions = "players") target: Player) {
        magenta.schedulerMagenta.doSync(magenta) {
            player.openInventory(target.inventory)
        }
    }
}