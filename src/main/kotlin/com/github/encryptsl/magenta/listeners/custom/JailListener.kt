package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.PlayerAccount
import com.github.encryptsl.magenta.api.events.jail.JailEvent
import com.github.encryptsl.magenta.common.PlayerCooldownManager
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.time.Duration

class JailListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onJail(event: JailEvent) {
        val commandManager = event.commandSender
        val jailName = event.jail
        val target = event.target
        val jailTime = event.jailTime
        val account = PlayerAccount(magenta, target.uniqueId)

        val cooldownManager = PlayerCooldownManager(target.uniqueId, magenta, "jail")

        if (target.player?.hasPermission("") == true)

        if (magenta.jailConfig.getJail().getConfigurationSection(jailName) == null)
            return commandManager.sendMessage(
                ModernText.miniModernText(
                    magenta.localeConfig.getMessage("magenta.command.jail.error.exist"), TagResolver.resolver(
                        Placeholder.parsed("jail", jailName)
                    )
                )
            )

        if (cooldownManager.hasCooldown() && account.getAccount().getBoolean("jailed"))
            return commandManager.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.jail.error.jailed"), TagResolver.resolver(
                Placeholder.parsed("player", target.name.toString())
            )))

        cooldownManager.setCooldown(Duration.ofSeconds(jailTime))
        account.getAccount().set("jailed", true)
        account.save()
        account.reload()
    }

}