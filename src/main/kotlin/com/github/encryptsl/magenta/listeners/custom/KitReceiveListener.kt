package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.account.PlayerAccount
import com.github.encryptsl.magenta.api.events.kit.KitReceiveEvent
import com.github.encryptsl.magenta.common.CommandHelper
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.time.Duration

class KitReceiveListener(private val magenta: Magenta) : Listener {

    private val commandHelper: CommandHelper by lazy { CommandHelper(magenta) }

    @EventHandler
    fun onKitReceive(event: KitReceiveEvent) {
        val player = event.player
        val kitName = event.kitName
        val delay = event.delay
        val kitManager = event.kitManager
        val playerAccount = PlayerAccount(magenta, player.uniqueId)

        val timeLeft: Duration = playerAccount.cooldownManager.getRemainingDelay("kits.$kitName")

        if (!playerAccount.cooldownManager.hasDelay("kits.$kitName")) {
            runCatching {
                kitManager.giveKit(player, kitName)
            }.onSuccess {
                if (delay != 0L && delay != -1L) {
                    if (!player.hasPermission("magenta.kit.delay.exempt")) {
                        playerAccount.cooldownManager.setDelay(Duration.ofSeconds(delay), "kits.$kitName")
                        playerAccount.save()
                    }
                }
                player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.kit.success.given.self"), TagResolver.resolver(
                    Placeholder.parsed("kit", kitName)
                )))
            }.onFailure { e ->
                player.sendMessage(ModernText.miniModernText(e.message ?: e.localizedMessage))
            }
        } else {
            commandHelper.delayMessage(player, "magenta.command.kit.error.delay", timeLeft)
        }
    }

}