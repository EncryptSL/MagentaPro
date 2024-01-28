package com.github.encryptsl.magenta.common.hook.luckperms

import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.cacheddata.CachedMetaData
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

class LuckPermsAPI {

    fun setupLuckPerms(): Boolean {
        return try {
            Class.forName("net.luckperms.api.LuckPerms")
            true
        } catch (e : ClassNotFoundException) {
            false
        }
    }

    fun hasPermission(player: OfflinePlayer, permission: String): Boolean
    {
        if (!setupLuckPerms())
            return player.player?.hasPermission(permission) == true
        return getLuckPerms().getPlayerAdapter(OfflinePlayer::class.java).getPermissionData(player).checkPermission(permission).asBoolean()
    }

    fun getGroup(player: Player): String {
        if (!setupLuckPerms())
            throw Exception("LuckPerms Missing")

        val metaData: CachedMetaData = getLuckPerms().getPlayerAdapter(Player::class.java).getMetaData(player)
        val group = metaData.primaryGroup ?: ""

        return group
    }

    fun getPrefix(player: Player): String {
        if (!setupLuckPerms())
            throw Exception("LuckPerms Missing")

        val metaData: CachedMetaData = getLuckPerms().getPlayerAdapter(Player::class.java).getMetaData(player)
        val prefix = metaData.prefix ?: ""

        return prefix
    }

    fun getSuffix(player: Player): String {
        if (!setupLuckPerms())
            throw Exception("LuckPerms Missing")

        val metaData: CachedMetaData = getLuckPerms().getPlayerAdapter(Player::class.java).getMetaData(player)
        val suffix = metaData.suffix ?: ""

        return suffix
    }

    fun getMetaValue(player: Player, value: String): String {
        if (!setupLuckPerms())
            throw Exception("LuckPerms Missing")

        val metaData: CachedMetaData = getLuckPerms().getPlayerAdapter(Player::class.java).getMetaData(player)

        return metaData.getMetaValue(value) ?: ""
    }

    private fun getLuckPerms(): LuckPerms {
        if (!setupLuckPerms())
            throw Exception("LuckPerms Missing")

        return LuckPermsProvider.get()
    }
}