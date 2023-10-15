package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.shop.credits.CreditShop
import com.github.encryptsl.magenta.api.shop.vault.VaultShop
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class ShopCmd(private val magenta: Magenta) {

    private val vaultShop = VaultShop(magenta)
    private val zeusShop = CreditShop(magenta)

    @CommandMethod("shop")
    @CommandPermission("magenta.shop")
    fun onShop(player: Player) {
        magenta.schedulerMagenta.runTask(magenta) {
            vaultShop.openShop(player)
        }
    }

    @CommandMethod("shop open <type>")
    @CommandPermission("magenta.shop.category")
    fun onShopOpen(player: Player, @Argument(value = "type", suggestions = "shops") type: String) {
        magenta.schedulerMagenta.runTask(magenta){
            vaultShop.openCategory(player, type)
        }
    }

    @CommandMethod("shop zeus")
    @CommandPermission("magenta.shop.zeus")
    fun onShopZeus(player: Player) {
        magenta.schedulerMagenta.runTask(magenta) {
            zeusShop.openShop(player)
        }
    }

    @CommandMethod("shop zeus open <type>")
    @CommandPermission("magenta.shop.zeus.category")
    fun onShopOpenZeus(player: Player, @Argument(value = "type", suggestions = "zeusShops") type: String) {
        magenta.schedulerMagenta.runTask(magenta){
            zeusShop.openCategory(player, type)
        }
    }

}