package com.github.encryptsl.magenta.api

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.Permissions
import org.bukkit.entity.Player
import java.math.BigDecimal

class Trader(private val magenta: Magenta) {
    fun getCommandCost(player: Player, command: String): BigDecimal {
        return when(player.hasPermission(Permissions.NON_COMMAND_COST)) {
            true -> BigDecimal.ZERO
            false -> magenta.config.getInt("commands-cost.$command", 0).toBigDecimal()
        }
    }
}