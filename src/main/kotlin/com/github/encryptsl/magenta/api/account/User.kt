package com.github.encryptsl.magenta.api.account

import com.github.encryptsl.magenta.api.account.models.UserAccountImpl
import org.bukkit.entity.Player
import java.util.*

class User {
    fun getUser(uuid: UUID): UserAccountImpl = UserAccountImpl(uuid)
    fun getUser(player: Player): UserAccountImpl = getUser(player.uniqueId)
}