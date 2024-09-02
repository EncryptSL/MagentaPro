package com.github.encryptsl.magenta.api.account.models

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.Permissions
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.time.Duration
import java.time.Instant
import java.util.*

class UserAccountImpl(uuid: UUID) : UserAccountAbstract(uuid) {

    override fun createDefaultData(player: Player) {
        set(mutableMapOf(
            "teleportenabled" to false,
            "jailed" to false,
            "afk" to false,
            "vanished" to false,
            "ip-address" to player.address.address.hostAddress,
            "socialspy" to false,
            "timestamps.lastteleport" to 0,
            "timestamps.lastheal" to 0,
            "timestamps.jail" to 0,
            "timestamps.onlinejail" to 0,
            "timestamps.onlinejail" to 0,
            "timestamps.logout" to 0,
            "timestamps.login" to System.currentTimeMillis(),
            "lastlocation" to player.location
        ))
    }

    override fun saveLastLocation(player: Player) {
        if (isJailed() && hasPunish()) return
        getAccount().set("lastlocation", player.location)
        save()
    }

    override fun saveQuitData(player: Player) {
        getAccount().set("timestamps.logout", System.currentTimeMillis())
        saveLastLocation(player)
        save()
    }

    override fun setTeleportEnabled(boolean: Boolean) {
        getAccount().set("teleportenabled", boolean)
        save()
    }

    override fun forceVanish() {
        if (!Magenta.instance.config.getBoolean("vanish-on-join")) return

        if (isVanished() && getPlayer()?.hasPermission(Permissions.VANISH_USE) != true) {
            set("vanished", false)
            return
        }

        for (onlinePlayers in Bukkit.getOnlinePlayers()) {
            if (onlinePlayers.equals(getPlayer())) continue

            if (!onlinePlayers.hasPermission(Permissions.VANISH_EXEMPT)) {
                if (isVanished()) {
                    getPlayer()?.let {
                        onlinePlayers.hidePlayer(Magenta.instance, it)
                        onlinePlayers.unlistPlayer(it)
                    }
                }
            }
        }
    }

    override fun addToIgnore(uuid: UUID) {
        val list: MutableList<String> = getAccount().getStringList("ignore")
        list.add(uuid.toString())
        set("ignore", list)
    }

    override fun removeIgnoredPlayer(uuid: UUID) {
        val list: MutableList<String> = getAccount().getStringList("ignore")
        list.remove(uuid.toString())
        set("ignore", list)
    }

    override fun setJailTimeout(seconds: Long) {
        setDelay(Duration.ofSeconds(seconds), "jail")
    }

    override fun setOnlineTime(millis: Long) {
        val onlineTime = Magenta.instance.config.getBoolean("online-jail-time")
        set("timestamps.onlinejail", if (onlineTime) millis else 0)
    }

    override fun setDelay(duration: Duration?, type: String) {
        set("timestamps.$type", Instant.now().plus(duration).toEpochMilli())
    }

    override fun resetDelay(type: String) {
        set("timestamps.$type", 0)
    }

    override fun set(path: String, value: Any?) {
        getAccount().set(path, value)
        save()
    }

    override fun set(path: MutableMap<String, Any>) {
        for (i in path) { getAccount().set(i.key, i.value) }
        save()
    }

    override fun set(path: String, list: MutableList<Any>) {
        getAccount().set(path, list)
        save()
    }

    override fun save() {
        universalConfig.save()
    }
}