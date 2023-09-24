package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.kit.KitReceiveEvent
import com.github.encryptsl.magenta.common.PlayerCooldownManager
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.time.Duration

class KitRecieveListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onKitReceive(event: KitReceiveEvent) {
        val player = event.player
        val kitName = event.kitName
        val cooldown = event.cooldown
        val kitManager = event.kitManager
        val cooldownManager = PlayerCooldownManager(player.uniqueId, magenta, "kits.$kitName")

        val timeLeft: Duration = cooldownManager.getRemainingCooldown()

        if (!cooldownManager.hasCooldown()) {
            kitManager.giveKit(player, kitName)
            cooldownManager.setCooldown(Duration.ofSeconds(cooldown))
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.kit.success.given.self"), TagResolver.resolver(
                Placeholder.parsed("kit", kitName)
            )))
        } else {
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.kit.error.delay"), TagResolver.resolver(
                Placeholder.parsed("timeleft", timeLeft.toSeconds().toString())
            )))
        }
    }

}