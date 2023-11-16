package com.github.encryptsl.magenta.common.hook.luckperms

import com.github.encryptsl.magenta.Magenta
import net.luckperms.api.LuckPerms
import org.bukkit.OfflinePlayer
import org.bukkit.plugin.ServicesManager

class LuckPermsAPI(private val magenta: Magenta) {

    private val plugin = "LuckPerms"

    private lateinit var luckPerms: LuckPerms

    fun setupLuckPerms(): Boolean {
        if (magenta.pluginManager.getPlugin(plugin) != null && magenta.pluginManager.isPluginEnabled(plugin)) {
            val sm: ServicesManager = magenta.server.servicesManager
            this.luckPerms = sm.load(LuckPerms::class.java)!!

            return true
        }

        return false
    }

    fun hasPermission(player: OfflinePlayer, permission: String): Boolean
    {
        if (!setupLuckPerms())
            return player.player?.hasPermission(permission) == true
        return getLuckPerms().getPlayerAdapter(OfflinePlayer::class.java).getPermissionData(player).checkPermission(permission).asBoolean()
    }

    fun getLuckPerms(): LuckPerms {
        if (!setupLuckPerms())
            throw Exception("LuckPerms Missing")

        return this.luckPerms
    }
}