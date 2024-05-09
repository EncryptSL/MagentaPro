package com.github.encryptsl.magenta.common.hook.luckperms

import com.github.encryptsl.kmono.lib.api.hook.PluginHook
import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.cacheddata.CachedMetaData
import net.luckperms.api.model.user.User
import net.luckperms.api.node.Node
import net.luckperms.api.node.NodeType
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.time.Instant
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.jvm.optionals.getOrNull


class LuckPermsAPI : PluginHook("LuckPerms") {

    fun hasPermission(player: OfflinePlayer, permission: String): Boolean
    {
        if (!isPluginEnabled())
            return player.player?.hasPermission(permission) == true

        return getUser(player.uniqueId).get().cachedData.permissionData.checkPermission(permission).asBoolean()
    }

    fun getGroup(player: Player): String {
        if (!isPluginEnabled())
            throw Exception("LuckPerms Missing")

        val metaData: CachedMetaData = getLuckPerms().getPlayerAdapter(Player::class.java).getMetaData(player)
        val group = metaData.primaryGroup ?: ""

        return group
    }

    fun getPrefix(player: Player): String {
        if (!isPluginEnabled())
            throw Exception("LuckPerms Missing")

        val metaData: CachedMetaData? = getLuckPerms().getPlayerAdapter(Player::class.java).getMetaData(player)
        val prefix = metaData?.prefix ?: "[DEFAULT]"

        return prefix
    }

    fun getSuffix(player: Player): String {
        if (!isPluginEnabled())
            throw Exception("LuckPerms Missing")

        val metaData: CachedMetaData? = getLuckPerms().getPlayerAdapter(Player::class.java).getMetaData(player)
        val suffix = metaData?.suffix ?: ""

        return suffix
    }

    fun getExpireGroup(offlinePlayer: OfflinePlayer, group: String): Instant? {
        if (!isPluginEnabled())
            throw Exception("LuckPerms Missing")

        return getUser(offlinePlayer.uniqueId)
            .get().getNodes(NodeType.INHERITANCE)
            .stream()
            .filter(Node::hasExpiry)
            .filter { n -> !n.hasExpired() }
            .filter { n -> n.groupName.equals(group, true)}.findFirst().getOrNull()?.expiry
    }

    fun getMetaValue(player: Player, value: String): String {
        if (!isPluginEnabled())
            throw Exception("LuckPerms Missing")

        val metaData: CachedMetaData? = getLuckPerms().getPlayerAdapter(Player::class.java).getMetaData(player)

        return metaData?.getMetaValue(value) ?: ""
    }

    fun getUser(uuid: UUID): CompletableFuture<User> {
        return getLuckPerms().userManager.loadUser(uuid)
    }

    private fun getLuckPerms(): LuckPerms {
        if (!isPluginEnabled())
            throw Exception("LuckPerms Missing")

        return LuckPermsProvider.get()
    }
}