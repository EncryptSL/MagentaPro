package com.github.encryptsl.magenta.api.menu.modules.shop.economy.components


import com.github.encryptsl.magenta.api.menu.modules.shop.economy.Economy
import com.github.encryptsl.magenta.api.menu.modules.shop.economy.EconomyTransaction
import com.github.encryptsl.magenta.api.menu.modules.shop.economy.MissingEconomyException
import com.github.encryptsl.magenta.api.menu.modules.shop.economy.TransactionProcess
import com.github.encryptsl.magenta.common.utils.ModernText
import org.bukkit.entity.Player

class EconomyWithdraw(private val player: Player, private val price: Double) : EconomyTransaction {
    override fun transaction(economy: Economy): TransactionProcess? {
        return try {
            if (!economy.hasBalance(player, price))
                return TransactionProcess.ERROR_ENOUGH_BALANCE

            economy.withdraw(player, price)
            return TransactionProcess.SUCCESS
        } catch (e : MissingEconomyException) {
            player.sendMessage(ModernText.miniModernText(e.message ?: e.localizedMessage))
            null
        }
    }
}