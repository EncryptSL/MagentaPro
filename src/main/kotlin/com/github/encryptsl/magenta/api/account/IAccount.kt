package com.github.encryptsl.magenta.api.account

import org.bukkit.Location
import org.bukkit.configuration.file.FileConfiguration

interface IAccount {

    fun set(path: String, value: Any?)
    fun save()
    fun isJailed(): Boolean
    fun isSocialSpy(): Boolean
    fun getLastLocation(): Location
    fun getAccount(): FileConfiguration
}