package com.github.encryptsl.magenta.api.account

import com.github.encryptsl.magenta.api.account.models.UserAccountImpl
import org.bukkit.plugin.Plugin
import java.util.*

class User(private val plugin: Plugin) {
    fun getUser(uuid: UUID): UserAccountImpl {
        return UserAccountImpl(plugin, uuid)
    }
}