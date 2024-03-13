package com.github.encryptsl.magenta.api.menu.shop.economy.components

import com.github.encryptsl.magenta.api.menu.shop.economy.Economy
import com.github.encryptsl.magenta.api.menu.shop.economy.EconomyTransaction
import com.github.encryptsl.magenta.api.menu.shop.economy.MissingEconomyException
import com.github.encryptsl.magenta.api.menu.shop.economy.TransactionProcess
import com.github.encryptsl.magenta.common.utils.ModernText
import org.bukkit.entity.Player

class EconomyDeposit(private val player: Player, private val price: Double) : EconomyTransaction {
    override fun transaction(economy: Economy): TransactionProcess? {
        return try {
            economy.deposit(player, price)
            return TransactionProcess.SUCCESS
        } catch (e : MissingEconomyException) {
            player.sendMessage(ModernText.miniModernText(e.message ?: e.localizedMessage))
            null
        }
    }
}