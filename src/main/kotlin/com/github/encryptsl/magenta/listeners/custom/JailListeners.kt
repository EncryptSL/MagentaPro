package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.InfoType
import com.github.encryptsl.magenta.api.events.jail.JailCheckEvent
import com.github.encryptsl.magenta.api.events.jail.JailCreateEvent
import com.github.encryptsl.magenta.api.events.jail.JailDeleteEvent
import com.github.encryptsl.magenta.api.events.jail.JailEvent
import com.github.encryptsl.magenta.api.events.jail.JailInfoEvent
import com.github.encryptsl.magenta.api.events.jail.JailPardonEvent
import com.github.encryptsl.magenta.api.events.jail.JailTeleportEvent
import com.github.encryptsl.magenta.api.scheduler.SchedulerMagenta
import com.github.encryptsl.magenta.common.hook.luckperms.LuckPermsAPI
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class JailListeners(private val magenta: Magenta) : Listener {

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

    @EventHandler
    fun onJailWhileJoin(event: JailCheckEvent) {
        val player = event.player
        val user = magenta.user.getUser(player.uniqueId)

        if (user.isJailed()) {
            val jailSection = magenta.jailConfig.getConfig().getConfigurationSection("jails") ?: return
            val randomJail = jailSection.getKeys(false).random()
            player.sendMessage(magenta.localeConfig.translation("magenta.command.jail.success.jailed"))

            player.teleport(magenta.jailManager.getJailLocation(randomJail))

            event.isCancelled = true
        }

        event.isCancelled = false
    }

    @EventHandler
    fun onCreateJail(event: JailCreateEvent) {
        val player = event.player
        val jailName = event.jailName
        val location = event.location

        if (magenta.jailConfig.getConfig().getConfigurationSection(jailName) != null)
            return player.sendMessage(magenta.localeConfig.translation("magenta.command.jail.error.exist",
                Placeholder.parsed("jail", jailName)
            ))


        val jailSection = magenta.jailConfig.getConfig().getConfigurationSection("jails") ?: return

        jailSection.set("$jailName.location.world", location.world.name)
        jailSection.set("$jailName.location.x", location.x)
        jailSection.set("$jailName.location.y", location.y)
        jailSection.set("$jailName.location.z", location.z)
        jailSection.set("$jailName.location.yaw", location.yaw)
        jailSection.set("$jailName.location.pitch", location.yaw)
        magenta.jailConfig.save()
        magenta.jailConfig.reload()

        return player.sendMessage(magenta.localeConfig.translation("magenta.command.jail.success.created", Placeholder.parsed("jail", jailName)))
    }

    @EventHandler
    fun onDeleteJail(event: JailDeleteEvent) {
        val commandSender = event.commandSender
        val jailName = event.jailName

        try {
            val jails = magenta.jailConfig.getConfig().getConfigurationSection("jails")
            jails?.set(jailName, null)
            magenta.jailConfig.save()
            magenta.jailConfig.reload()
            commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.jail.success.deleted", Placeholder.parsed("jail", jailName)))
        } catch (_ : Exception) {
            commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.jail.error.not.exist", Placeholder.parsed("jail", jailName)))
        }
    }

    @EventHandler
    fun onJailRelease(event: JailPardonEvent) {
        val target = event.player
        val user = magenta.user.getUser(target.uniqueId)

        val player = Bukkit.getPlayer(target.uniqueId)

        if (!user.isJailed() && !user.hasPunish()) return

        player?.sendMessage(magenta.localeConfig.translation("magenta.command.jail.success.unjailed"))

        player?.let { magenta.pluginManager.callEvent(JailTeleportEvent(player, user.getLastLocation())) }

        Bukkit.broadcast(magenta.localeConfig.translation("magenta.command.jail.success.unjailed.to",
            Placeholder.parsed("player", target.name.toString())
        ), "magenta.jail.pardon.event")

        user.resetDelay("jail")
        user.resetDelay("onlinejail")
    }

    @EventHandler
    fun onJailTeleport(event: JailTeleportEvent) {
        val player = event.target
        val location = event.location

        SchedulerMagenta.doSync(magenta) {
            player.teleport(location)
        }
    }

    @EventHandler
    fun onJailInfo(event: JailInfoEvent) {
        val commandSender = event.commandSender
        val infoType = event.infoType

        when (infoType) {
            InfoType.LIST -> {
                jailList(commandSender)
            }
            InfoType.INFO -> {
                val jailName = event.jailName ?: return
                jailInfo(commandSender, jailName)
            }
        }
    }

    private fun jailList(commandSender: CommandSender) {
        val jailSection = magenta.jailConfig.getConfig().getConfigurationSection("jails") ?: return
        val list = jailSection.getKeys(false).joinToString { s ->
            magenta.localeConfig.getMessage("magenta.command.jail.success.list.component").replace("<jail>", s)
                .replace("<info>", magenta.config.getString("jail-info-format").toString()
                    .replace("<jail>", s)
                    .replace("<world>", jailSection.getString("$s.location.world").toString())
                    .replace("<x>", jailSection.getString("$s.location.x").toString())
                    .replace("<y>", jailSection.getString("$s.location.y").toString())
                    .replace("<z>", jailSection.getString("$s.location.z").toString())
                )
        }
        commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.jail.success.list",
            Placeholder.component("jails", ModernText.miniModernText(list))
        ))
    }

    private fun jailInfo(commandSender: CommandSender, jailName: String) {
        val jailSection = magenta.jailConfig.getConfig().getConfigurationSection("jails") ?: return
        if (!jailSection.contains(jailName))
            return commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.warp.error.not.exist"))

        commandSender.sendMessage(ModernText.miniModernText(magenta.config.getString("jail-info-format").toString(), TagResolver.resolver(
            Placeholder.parsed("jail", jailName),
            Placeholder.parsed("world", jailSection.getString("$jailName.location.world").toString()),
            Placeholder.parsed("x", jailSection.getString("$jailName.location.x").toString()),
            Placeholder.parsed("y", jailSection.getString("$jailName.location.y").toString()),
            Placeholder.parsed("z", jailSection.getString("$jailName.location.z").toString()),
        )))
    }




}