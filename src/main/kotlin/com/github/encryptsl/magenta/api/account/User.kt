package com.github.encryptsl.magenta.api.account

import com.github.encryptsl.magenta.Magenta
import java.util.*

class User(private val magenta: Magenta) {
    fun getUser(uuid: UUID): PlayerAccount {
        return PlayerAccount(magenta, uuid)
    }
}