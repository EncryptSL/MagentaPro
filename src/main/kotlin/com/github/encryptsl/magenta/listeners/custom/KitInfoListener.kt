package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.InfoType
import com.github.encryptsl.magenta.api.events.kit.KitInfoEvent
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class KitInfoListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onKitList(event: KitInfoEvent) {
        val commandSender = event.commandSender
        val infoType = event.infoType

        when(infoType) {
            InfoType.LIST -> {
                val section = magenta.kitConfig.getKit().getConfigurationSection("kits") ?: return

                val list = section.getKeys(false).joinToString { s ->
                    magenta.localeConfig.getMessage("magenta.command.kit.success.list.component").replace("<kit>", s)
                }
                commandSender.sendMessage(
                    ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.kit.success.list"), TagResolver.resolver(
                    Placeholder.component("kits", ModernText.miniModernText(list))
                )))
            }
            InfoType.INFO -> {
                val kitName = event.kitName ?: return
                if (magenta.kitConfig.getKit().getConfigurationSection("kits.$kitName") == null)
                    return commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.kit.error.not.exist")))
            }
        }
    }

}