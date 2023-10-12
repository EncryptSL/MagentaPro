package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.shop.ShopManager
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class ShopCmd(private val magenta: Magenta) {

    private val shopManager = ShopManager(magenta)

    @CommandMethod("shop")
    @CommandPermission("magenta.shop")
    fun onShop(player: Player) {
        magenta.schedulerMagenta.runTask(magenta) {
            shopManager.openShop(player)
        }
    }

    @CommandMethod("shop open <type>")
    @CommandPermission("magenta.shop.category")
    fun onShopOpen(player: Player, @Argument(value = "type", suggestions = "shops") type: String) {
        magenta.schedulerMagenta.runTask(magenta){
            shopManager.openCategory(player, type)
        }
    }

}