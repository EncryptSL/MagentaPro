package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.Command
import cloud.commandframework.annotations.Permission
import com.github.encryptsl.magenta.Magenta
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class EnderChestCmd(private val magenta: Magenta) {

    @Command("echest|enderchest")
    @Permission("magenta.echest")
    fun onEnderChest(player: Player) {
        magenta.schedulerMagenta.doSync(magenta) {
            player.openInventory(player.enderChest)
        }
    }

    @Command("echest|enderchest <player>")
    @Permission("magenta.echest.other")
    fun onEnderChestOther(
        player: Player,
        @Argument(value = "player", suggestions = "players") target: Player
    ) {
        magenta.schedulerMagenta.doSync(magenta) {
            player.openInventory(target.enderChest)
        }
    }

}