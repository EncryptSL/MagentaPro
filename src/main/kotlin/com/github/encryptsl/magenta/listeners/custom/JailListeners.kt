package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.InfoType
import com.github.encryptsl.magenta.api.events.jail.*
import com.github.encryptsl.magenta.common.Permissions
import com.github.encryptsl.magenta.common.hook.luckperms.LuckPermsAPI
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
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

        if (luckPerms.hasPermission(target,Permissions.JAIL_PLAYER_EXEMPT)) return

        if (magenta.jailConfig.getConfig().getConfigurationSection("jails.$jailName") == null)
            return commandManager.sendMessage(magenta.locale.translation("magenta.command.jail.error.exist",
                Placeholder.parsed("jail", jailName)
            ))

        if (user.isJailed())
            return commandManager.sendMessage(magenta.locale.translation("magenta.command.jail.error.jailed",
                Placeholder.parsed("player", target.name.toString())
            ))

        if (target.player != null) {
            target.player?.sendMessage(magenta.locale.translation("magenta.command.jail.success.jailed"))
            magenta.jailManager.teleport(target.player!!, jailName)
        }


        Bukkit.broadcast(magenta.locale.translation("magenta.command.jail.success.jailed.to", TagResolver.resolver(
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
            player.sendMessage(magenta.locale.translation("magenta.command.jail.success.jailed"))
            magenta.jailManager.teleport(player, randomJail)
            event.isCancelled = true
        }

        event.isCancelled = false
    }

    @EventHandler
    fun onCreateJail(event: JailCreateEvent) {
        val player = event.player
        val jailName = event.jailName
        val location = event.location

        magenta.jailConfig.getConfig().createSection("jails")
        val jailSection = magenta.jailConfig.getConfig().getConfigurationSection("jails") ?: return

        if (magenta.jailConfig.getConfig().getConfigurationSection("jails.$jailName") != null)
            return player.sendMessage(magenta.locale.translation("magenta.command.jail.error.exist",
                Placeholder.parsed("jail", jailName)
            ))

        jailSection.set("$jailName.location", location)
        magenta.jailConfig.save()

        return player.sendMessage(magenta.locale.translation("magenta.command.jail.success.created", Placeholder.parsed("jail", jailName)))
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
            commandSender.sendMessage(magenta.locale.translation("magenta.command.jail.success.deleted", Placeholder.parsed("jail", jailName)))
        } catch (_ : Exception) {
            commandSender.sendMessage(magenta.locale.translation("magenta.command.jail.error.not.exist", Placeholder.parsed("jail", jailName)))
        }
    }

    @EventHandler
    fun onJailRelease(event: JailPardonEvent) {
        val target = event.player
        val user = magenta.user.getUser(target.uniqueId)

        val player = Bukkit.getPlayer(target.uniqueId)

        if (!user.isJailed() && !user.hasPunish()) return

        player?.sendMessage(magenta.locale.translation("magenta.command.jail.success.unjailed"))

        player?.let { player.teleportAsync(user.getLastLocation()) }

        Bukkit.broadcast(magenta.locale.translation("magenta.command.jail.success.unjailed.to",
            Placeholder.parsed("player", target.name.toString())
        ), "magenta.jail.pardon.event")

        user.resetDelay("jail")
        user.resetDelay("onlinejail")
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
            val location = magenta.jailManager.getJailLocation(s)
            magenta.locale.getMessage("magenta.command.jail.success.list.component").replace("<jail>", s)
                .replace("<info>", magenta.config.getString("jail-info-format").toString()
                    .replace("<jail>", s)
                    .replace("<world>", location.world.name)
                    .replace("<x>", location.x().toString())
                    .replace("<y>", location.y().toString())
                    .replace("<z>", location.z().toString())
                )
        }
        commandSender.sendMessage(magenta.locale.translation("magenta.command.jail.success.list",
            Placeholder.component("jails", ModernText.miniModernText(list))
        ))
    }

    private fun jailInfo(commandSender: CommandSender, jailName: String) {
        val jailSection = magenta.jailConfig.getConfig().getConfigurationSection("jails") ?: return
        if (!jailSection.contains(jailName))
            return commandSender.sendMessage(magenta.locale.translation("magenta.command.warp.error.not.exist"))

        val location = magenta.jailManager.getJailLocation(jailName)

        commandSender.sendMessage(ModernText.miniModernText(magenta.config.getString("jail-info-format").toString(), TagResolver.resolver(
            Placeholder.parsed("jail", jailName),
            Placeholder.parsed("world", location.world.name),
            Placeholder.parsed("x", location.x().toString()),
            Placeholder.parsed("y", location.y().toString()),
            Placeholder.parsed("z", location.z().toString()),
        )))
    }




}