package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.Command
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.Permission
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.shop.credits.CreditShop
import com.github.encryptsl.magenta.api.shop.vault.VaultShop
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class ShopCmd(private val magenta: Magenta) {

    private val vaultShop = VaultShop(magenta)
    private val creditShop = CreditShop(magenta)

    @Command("shop")
    @Permission("magenta.shop")
    fun onShop(player: Player) {
        magenta.schedulerMagenta.doSync(magenta) {
            vaultShop.openShop(player)
        }
    }

    @Command("shop open <type>")
    @Permission("magenta.shop.open")
    fun onShopOpen(player: Player, @Argument(value = "type", suggestions = "shops") type: String) {
        magenta.schedulerMagenta.doSync(magenta){
            vaultShop.openCategory(player, type)
        }
    }

    @Command("creditshop")
    @Permission("magenta.credit.shop")
    fun onShopZeus(player: Player) {
        magenta.schedulerMagenta.doSync(magenta) {
            creditShop.openShop(player)
        }
    }

    @Command("creditshop open <type>")
    @Permission("magenta.credit.shop.open")
    fun onShopOpenZeus(player: Player, @Argument(value = "type", suggestions = "creditshops") type: String) {
        magenta.schedulerMagenta.doSync(magenta){
            creditShop.openCategory(player, type)
        }
    }

}