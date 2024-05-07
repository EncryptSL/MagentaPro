package com.github.encryptsl.magenta.common.extensions

import org.bukkit.ChatColor
import java.util.*

private const val AVATAR = "https://visage.surgeplay.com/bust/%s.png?,shadow&y=-40"

fun UUID.trimUUID() = this.toString().replace("-","")
fun UUID.toMinotarAvatar() = AVATAR.format(this.trimUUID())

@Suppress("DEPRECATION")
fun colorize(value: String): String = ChatColor.translateAlternateColorCodes('&', value)

fun expressionCalculation(string: String): String {
    return evaluate(string).toInt().toString()
}

fun String.censorIpAddress(): String {
    val oktety = this.split(".")
    if (oktety.size != 4) {
        throw IllegalArgumentException("Not valid IP Address")
    }
    return "${oktety[0]}.${oktety[1]}.${oktety[2]}.xxx"
}