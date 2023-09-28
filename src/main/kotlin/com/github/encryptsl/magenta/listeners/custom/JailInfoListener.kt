package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.InfoType
import com.github.encryptsl.magenta.api.events.jail.JailInfoEvent
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class JailInfoListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onJailInfo(event: JailInfoEvent) {
        val commandSender = event.commandSender
        val infoType = event.infoType


        when (infoType) {
            InfoType.LIST -> {
                val section = magenta.jailConfig.getJail().getConfigurationSection("jails") ?: return

                val list = section.getKeys(false).joinToString { s ->
                    magenta.localeConfig.getMessage("magenta.command.jail.success.list.component").replace("<jail>", s)
                }
                commandSender.sendMessage(
                    ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.jail.success.list"), TagResolver.resolver(
                        Placeholder.component("jails", ModernText.miniModernText(list))
                    )))
            }
            InfoType.INFO -> {
                val jailName = event.jailName ?: return
                val jailSection = magenta.jailConfig.getJail().getConfigurationSection("jails") ?: return
                if (!jailSection.contains(jailName))
                    return commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.warp.error.not.exist")))


                magenta.config.getStringList("jail-info-format").forEach { s ->
                    commandSender.sendMessage(ModernText.miniModernText(s, TagResolver.resolver(
                        Placeholder.parsed("jail", jailSection.getString(jailName).toString()),
                        Placeholder.parsed("world", jailSection.getString("$jailName.location.world").toString()),
                        Placeholder.parsed("x", jailSection.getString("$jailName.location.x").toString()),
                        Placeholder.parsed("y", jailSection.getString("$jailName.location.y").toString()),
                        Placeholder.parsed("z", jailSection.getString("$jailName.location.z").toString()),
                    )))
                }
            }
        }


    }

}