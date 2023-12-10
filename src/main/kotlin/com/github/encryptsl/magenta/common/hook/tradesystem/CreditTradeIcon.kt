package com.github.encryptsl.magenta.common.hook.tradesystem

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.hook.creditlite.CreditLiteHook
import de.codingair.tradesystem.spigot.extras.external.EconomySupportType
import de.codingair.tradesystem.spigot.extras.external.TypeCap
import de.codingair.tradesystem.spigot.trade.gui.layout.types.impl.economy.EconomyIcon
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.math.BigDecimal

class CreditTradeIcon(
    val magenta: Magenta,
    itemStack: ItemStack
) : EconomyIcon<ShowCreditTradeIcon>(itemStack, "Credit", "Credits", true) {

    private val creditLiteHook: CreditLiteHook by lazy { CreditLiteHook(magenta) }

    override fun getTargetClass(): Class<ShowCreditTradeIcon> {
        return ShowCreditTradeIcon::class.java
    }

    override fun getBalance(player: Player): BigDecimal {
        return creditLiteHook.getCredits(player).toBigDecimal()
    }

    override fun withdraw(player: Player, value: BigDecimal) {
        creditLiteHook.withdrawCredits(player, value.toDouble())
    }

    override fun deposit(player: Player, value: BigDecimal) {
        creditLiteHook.depositCredits(player, value.toDouble())
    }

    override fun getMaxSupportedValue(): TypeCap {
        return EconomySupportType.DOUBLE
    }
}