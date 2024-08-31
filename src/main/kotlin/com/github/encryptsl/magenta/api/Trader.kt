package com.github.encryptsl.magenta.api

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.Permissions
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.entity.Player
import java.math.BigDecimal

class Trader(private val magenta: Magenta) {
    fun getCommandCost(player: Player, command: String): BigDecimal {
        return when(player.hasPermission(Permissions.NON_COMMAND_COST) || player.hasPermission(Permissions.NON_COMMAND_COST_COMMAND.format(command))) {
            true -> BigDecimal.ZERO
            false -> magenta.config.getInt("commands-cost.$command", 0).toBigDecimal()
        }
    }

    fun notEnoughDollars(player: Player) {
        player.sendMessage(magenta.locale.translation("magenta.error.not.enough.balance.to.use.command"))
    }

    fun successTradeWithDraw(player: Player, cost: BigDecimal) {
        if (cost == BigDecimal.ZERO) return
        player.sendMessage(magenta.locale.translation("magenta.success.economy.withdraw", Placeholder.parsed("cost", cost.toPlainString())))
    }
}