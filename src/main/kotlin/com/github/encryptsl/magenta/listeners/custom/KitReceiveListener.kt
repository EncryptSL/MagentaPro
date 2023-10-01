package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.PlayerAccount
import com.github.encryptsl.magenta.api.events.kit.KitReceiveEvent
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.time.Duration

class KitReceiveListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onKitReceive(event: KitReceiveEvent) {
        val player = event.player
        val kitName = event.kitName
        val cooldown = event.cooldown
        val kitManager = event.kitManager
        val playerAccount = PlayerAccount(magenta, player.uniqueId)

        val timeLeft: Duration = playerAccount.cooldownManager.getRemainingCooldown("kits.$kitName")

        if (!playerAccount.cooldownManager.hasCooldown("kits.$kitName")) {
            runCatching {
                kitManager.giveKit(player, kitName)
            }.onSuccess {
                if (cooldown != 0L && cooldown != -1L) {
                    playerAccount.cooldownManager.setCooldown(Duration.ofSeconds(cooldown), "kits.$kitName")
                    playerAccount.save()
                }
                player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.kit.success.given.self"), TagResolver.resolver(
                    Placeholder.parsed("kit", kitName)
                )))
            }.onFailure { e ->
                player.sendMessage(ModernText.miniModernText(e.message ?: e.localizedMessage))
            }
        } else {
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.kit.error.delay"), TagResolver.resolver(
                Placeholder.parsed("timeleft", timeLeft.toSeconds().toString())
            )))
        }
    }

}