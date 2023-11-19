package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
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
        val user = magenta.user.getUser(player.uniqueId)

        val timeLeft: Duration = user.cooldownManager.getRemainingDelay("kits.$kitName")

        if (user.cooldownManager.hasDelay("kits.$kitName") && !player.hasPermission("magenta.kit.delay.exempt"))
            return commandHelper.delayMessage(player, "magenta.command.kit.error.delay", timeLeft)

        try {
            if (delay != 0L && delay != -1L) {
                if (!player.hasPermission("magenta.kit.delay.exempt")) {
                    user.cooldownManager.setDelay(Duration.ofSeconds(delay), "kits.$kitName")
                    user.save()
                }
            }
            kitManager.giveKit(player, kitName)
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.kit.success.given.self"), TagResolver.resolver(
                Placeholder.parsed("kit", kitName)
            )))
        } catch (e : Exception) {
            player.sendMessage(ModernText.miniModernText(e.message ?: e.localizedMessage))
        }
    }

}