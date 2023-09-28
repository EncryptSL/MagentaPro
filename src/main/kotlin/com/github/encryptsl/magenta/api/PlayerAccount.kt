package com.github.encryptsl.magenta.api

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ConfigUtil
import org.bukkit.configuration.file.FileConfiguration
import java.util.*

class PlayerAccount(magenta: Magenta, private val uuid: UUID) {

    private val configUtil = ConfigUtil(magenta, "/players/$uuid.yml")
    val cooldownManager: PlayerCooldown by lazy { PlayerCooldown(uuid, this) }

    fun save() {
        configUtil.save()
    }

    fun getAccount(): FileConfiguration {
        return configUtil.getConfig()
    }
}