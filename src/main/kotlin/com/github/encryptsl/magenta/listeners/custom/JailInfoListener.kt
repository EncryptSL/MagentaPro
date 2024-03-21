package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.InfoType
import com.github.encryptsl.magenta.api.events.jail.JailInfoEvent
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.command.CommandSender
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class JailInfoListener(private val magenta: Magenta) : Listener {

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


        magenta.config.getStringList("jail-info-format").forEach { s ->
            commandSender.sendMessage(ModernText.miniModernText(s, TagResolver.resolver(
                Placeholder.parsed("jail", jailName),
                Placeholder.parsed("world", jailSection.getString("$jailName.location.world").toString()),
                Placeholder.parsed("x", jailSection.getString("$jailName.location.x").toString()),
                Placeholder.parsed("y", jailSection.getString("$jailName.location.y").toString()),
                Placeholder.parsed("z", jailSection.getString("$jailName.location.z").toString()),
            )))
        }
    }

}