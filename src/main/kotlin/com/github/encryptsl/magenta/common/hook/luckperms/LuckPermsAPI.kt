package com.github.encryptsl.magenta.common.hook.luckperms

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

        var a = false

        getUser(player.uniqueId).thenAcceptAsync { user ->
           a = user.cachedData.permissionData.checkPermission(permission).asBoolean()
        }

        return a
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
        val prefix = metaData.prefix ?: "[DEFAULT]"

        return prefix
    }

    fun getSuffix(player: Player): String {
        if (!setupLuckPerms())
            throw Exception("LuckPerms Missing")

        val metaData: CachedMetaData = getLuckPerms().getPlayerAdapter(Player::class.java).getMetaData(player)
        val suffix = metaData.suffix ?: ""

        return suffix
    }

    fun getExpireGroup(offlinePlayer: OfflinePlayer, group: String): Instant? {
        if (!setupLuckPerms())
            throw Exception("LuckPerms Missing")

        var a: Instant? = null

        getUser(offlinePlayer.uniqueId).thenAcceptAsync { user ->
           a = user.getNodes(NodeType.INHERITANCE)
            .stream()
            .filter(Node::hasExpiry)
            .filter {node -> !node.hasExpired()}
            .filter { g -> g.groupName == group }
            .findFirst().get().expiry
        }

        return a
    }

    fun getMetaValue(player: Player, value: String): String {
        if (!setupLuckPerms())
            throw Exception("LuckPerms Missing")

        val metaData: CachedMetaData = getLuckPerms().getPlayerAdapter(Player::class.java).getMetaData(player)

        return metaData.getMetaValue(value) ?: ""
    }

    fun getUser(uuid: UUID): CompletableFuture<User> {
        return getLuckPerms().userManager.loadUser(uuid)
    }

    private fun getLuckPerms(): LuckPerms {
        if (!setupLuckPerms())
            throw Exception("LuckPerms Missing")

        return LuckPermsProvider.get()
    }
}