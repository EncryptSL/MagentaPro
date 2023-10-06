package com.github.encryptsl.magenta.api.account

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.JailManager
import com.github.encryptsl.magenta.api.PlayerCooldown
import com.github.encryptsl.magenta.common.utils.ConfigUtil
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.file.FileConfiguration
import java.util.*

class PlayerAccount(private val magenta: Magenta, private val uuid: UUID) : IAccount {

    private val configUtil = ConfigUtil(magenta, "/players/$uuid.yml")
    val cooldownManager: PlayerCooldown by lazy { PlayerCooldown(uuid, this) }
    val jailManager: JailManager by lazy { JailManager(magenta, this) }

    override fun set(path: String, value: Any?) {
        magenta.schedulerMagenta.runTaskAsync(magenta) {
            getAccount().set(path, value)
            save()
        }
    }

    override fun set(path: String, list: MutableList<Any>) {
        magenta.schedulerMagenta.runTaskAsync(magenta) {
            list.forEach { item ->
                getAccount().set(path, item)
            }
            save()
        }
    }

    override fun save() {
        configUtil.save()
    }

    override fun isJailed(): Boolean {
        return getAccount().getBoolean("jailed")
    }

    override fun isSocialSpy(): Boolean {
        return getAccount().getBoolean("socialspy")
    }

    override fun getVotifierRewards(): MutableList<String> {
        return getAccount().getStringList("votifier.rewards")
    }

    override fun getLastLocation(): Location {
        val world = getAccount().getString("lastlocation.world-name").toString()
        val x = getAccount().getDouble("lastlocation.x")
        val y = getAccount().getDouble("lastlocation.y")
        val z = getAccount().getDouble("lastlocation.z")
        val yaw = getAccount().getString("lastlocation.yaw").toString().toFloat()
        val pitch = getAccount().getString("lastlocation.pitch").toString().toFloat()
        return Location(Bukkit.getWorld(world), x, y, z, yaw, pitch)
    }

    override fun getAccount(): FileConfiguration {
        return configUtil.getConfig()
    }
}