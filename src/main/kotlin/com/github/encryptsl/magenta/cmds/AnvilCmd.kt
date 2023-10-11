package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.github.encryptsl.magenta.Magenta
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType

@CommandDescription("Provided by plugin MagentaPro")
class AnvilCmd(private val magenta: Magenta) {
    @CommandMethod("anvil")
    @CommandPermission("magenta.anvil")
    fun onAnvil(player: Player) {
        magenta.schedulerMagenta.runTask(magenta) {
            player.openInventory(Bukkit.createInventory(null, InventoryType.ANVIL))
        }
    }
}