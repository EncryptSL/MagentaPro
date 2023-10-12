package com.github.encryptsl.magenta.api.shop

import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player

object ShopHelper {
    @JvmStatic
    fun isPlayerInventoryFull(player: HumanEntity): Boolean
    {
        return player.inventory.firstEmpty() == -1
    }

    fun buyPrice(price: Int, amount: Int): Int
    {
        return price - amount
    }

    @JvmStatic
    fun sellPrice(price: Int, amount: Int): Int
    {
        return price * amount
    }
}