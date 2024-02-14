package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.shop.credits.CreditShop
import com.github.encryptsl.magenta.api.menu.shop.vault.VaultShop
import com.github.encryptsl.magenta.api.scheduler.SchedulerMagenta
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class ShopCmd(private val magenta: Magenta) {

    private val vaultShop = VaultShop(magenta)
    private val creditShop = CreditShop(magenta)

    @Command("shop")
    @Permission("magenta.shop")
    fun onShop(player: Player) {
        SchedulerMagenta.doSync(magenta) {
            vaultShop.openShop(player)
        }
    }

    @Command("shop open <type>")
    @Permission("magenta.shop.open")
    fun onShopOpen(player: Player, @Argument(value = "type", suggestions = "shops") type: String) {
        SchedulerMagenta.doSync(magenta){
            vaultShop.openCategory(player, type)
        }
    }

    @Command("creditshop")
    @Permission("magenta.credit.shop")
    fun onShopZeus(player: Player) {
        SchedulerMagenta.doSync(magenta) {
            creditShop.openShop(player)
        }
    }

    @Command("creditshop open <type>")
    @Permission("magenta.credit.shop.open")
    fun onShopOpenZeus(player: Player, @Argument(value = "type", suggestions = "creditshops") type: String) {
        SchedulerMagenta.doSync(magenta){
            creditShop.openCategory(player, type)
        }
    }

}