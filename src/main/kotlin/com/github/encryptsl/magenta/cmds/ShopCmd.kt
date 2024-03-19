package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.shop.credits.CreditShop
import com.github.encryptsl.magenta.api.menu.shop.vault.VaultShop
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

@Suppress("UNUSED")
class ShopCmd(magenta: Magenta) {

    private val vaultShop = VaultShop(magenta)
    private val creditShop = CreditShop(magenta)

    @Command("shop")
    @Permission("magenta.shop")
    @CommandDescription("This command open shop gui")
    fun onShop(player: Player) {
        vaultShop.openMenu(player)
    }

    @Command("shop open <type>")
    @Permission("magenta.shop.open")
    @CommandDescription("This command open shop category gui")
    fun onShopOpen(player: Player, @Argument(value = "type", suggestions = "shops") type: String) {
        vaultShop.openCategory(player, type)
    }

    @Command("creditshop|cshop")
    @Permission("magenta.credit.shop")
    @CommandDescription("This command open credit shop")
    fun onCreditShop(player: Player) {
        creditShop.openMenu(player)
    }

    @Command("creditshop|cshop open <type>")
    @Permission("magenta.credit.shop.open")
    @CommandDescription("This command open credit shop category")
    fun onOpenCreditShop(player: Player, @Argument(value = "type", suggestions = "creditshops") type: String) {
        creditShop.openCategory(player, type)
    }

}