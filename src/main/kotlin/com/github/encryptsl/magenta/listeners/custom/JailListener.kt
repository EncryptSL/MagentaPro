package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.jail.JailEvent
import com.github.encryptsl.magenta.api.events.jail.JailTeleportEvent
import com.github.encryptsl.magenta.common.hook.luckperms.LuckPermsAPI
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class JailListener(private val magenta: Magenta) : Listener {

    private val luckPerms: LuckPermsAPI by lazy { LuckPermsAPI() }

    @EventHandler
    fun onJail(event: JailEvent) {
        val commandManager = event.commandSender
        val jailName = event.jail
        val target = event.target
        val jailTime = event.jailTime
        val reason = event.reason
        val user = magenta.user.getUser(target.uniqueId)

        if (luckPerms.hasPermission(target,"magenta.jail.exempt")) return

        if (magenta.jailConfig.getConfig().getConfigurationSection("jails.$jailName") == null)
            return commandManager.sendMessage(magenta.localeConfig.translation("magenta.command.jail.error.exist",
                Placeholder.parsed("jail", jailName)
            ))

        if (user.isJailed())
            return commandManager.sendMessage(magenta.localeConfig.translation("magenta.command.jail.error.jailed",
                Placeholder.parsed("player", target.name.toString())
            ))

        if (target.player != null) {
            val jailSection = magenta.jailConfig.getConfig().getConfigurationSection("jails.$jailName") ?: return
            target.player?.sendMessage(magenta.localeConfig.translation("magenta.command.jail.success.jailed"))

            magenta.pluginManager.callEvent(JailTeleportEvent(target.player!!, Location(
                Bukkit.getWorld(jailSection.getString("location.world").toString()),
                jailSection.getDouble("location.x"),
                jailSection.getDouble("location.y"),
                jailSection.getDouble("location.z"),
                jailSection.getInt("location.yaw").toFloat(),
                jailSection.getInt("location.pitch").toFloat()
            )))
        }


        Bukkit.broadcast(magenta.localeConfig.translation("magenta.command.jail.success.jailed.to", TagResolver.resolver(
            Placeholder.parsed("player", target.name ?: target.uniqueId.toString()),
            Placeholder.parsed("reason", reason.toString()),
        )))
        user.setJailTimeout(jailTime)
        user.setOnlineTime(jailTime)
    }

}