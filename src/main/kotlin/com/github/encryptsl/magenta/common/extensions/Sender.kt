package com.github.encryptsl.magenta.common.extensions

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer

fun sendConsoleCommand(command: String, offlinePlayer: OfflinePlayer) {
    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", offlinePlayer.name.toString()).replace("%player%", offlinePlayer.name.toString()))
}