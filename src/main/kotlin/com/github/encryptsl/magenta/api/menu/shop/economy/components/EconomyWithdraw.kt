package com.github.encryptsl.magenta.api.menu.shop.economy.components

import com.github.encryptsl.magenta.api.menu.shop.economy.Economy
import com.github.encryptsl.magenta.api.menu.shop.economy.EconomyTransaction
import com.github.encryptsl.magenta.api.menu.shop.economy.MissingEconomyException
import com.github.encryptsl.magenta.api.menu.shop.economy.TransactionErrors
import com.github.encryptsl.magenta.common.utils.ModernText
import org.bukkit.entity.Player

class EconomyWithdraw(private val player: Player, private val price: Double) : EconomyTransaction {
    override fun transaction(economy: Economy): TransactionErrors? {
        return try {
            if (!economy.hasBalance(player, price))
                return TransactionErrors.ERROR_ENOUGH_BALANCE

            economy.withdraw(player, price)
            return TransactionErrors.SUCCESS
        } catch (e : MissingEconomyException) {
            player.sendMessage(ModernText.miniModernText(e.message ?: e.localizedMessage))
            null
        }
    }
}