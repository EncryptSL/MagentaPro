package com.github.encryptsl.magenta.api.shop

import org.bukkit.entity.HumanEntity

object ShopHelper {
    @JvmStatic
    fun isPlayerInventoryFull(player: HumanEntity): Boolean
    {
        return player.inventory.firstEmpty() == -1
    }

    fun calcPrice(amount: Int, price: Int): Int {
        return (amount.times(price))
    }
}