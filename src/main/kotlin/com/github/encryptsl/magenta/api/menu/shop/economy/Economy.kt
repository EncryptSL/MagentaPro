package com.github.encryptsl.magenta.api.menu.shop.economy

import org.bukkit.OfflinePlayer

interface Economy {
    fun hasBalance(player: OfflinePlayer, value: Double): Boolean
    fun deposit(player: OfflinePlayer, value: Double)
    fun withdraw(player: OfflinePlayer, value: Double)
    fun getBalance(player: OfflinePlayer): Double
}