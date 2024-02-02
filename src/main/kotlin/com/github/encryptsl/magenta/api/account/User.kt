package com.github.encryptsl.magenta.api.account

import org.bukkit.plugin.Plugin
import java.util.*

class User(private val plugin: Plugin) {
    fun getUser(uuid: UUID): UserAccount {
        return UserAccount(plugin, uuid)
    }
}