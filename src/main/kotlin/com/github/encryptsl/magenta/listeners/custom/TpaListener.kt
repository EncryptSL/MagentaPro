package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.teleport.TpaAcceptEvent
import com.github.encryptsl.magenta.api.events.teleport.TpaDenyEvent
import com.github.encryptsl.magenta.api.events.teleport.TpaRequestEvent
import com.github.encryptsl.magenta.api.scheduler.SchedulerMagenta
import com.github.encryptsl.magenta.common.PlayerBuilderAction
import com.github.encryptsl.magenta.common.utils.ModernText
import io.papermc.paper.util.Tick
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.time.Duration

class TpaListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onTpaAccept(event: TpaAcceptEvent) {
        val player = event.player
        magenta.tpaManager.acceptRequest(player)
    }

    @EventHandler
    fun onTpaDeny(event: TpaDenyEvent) {
        val sender = event.sender
        magenta.tpaManager.denyRequest(sender)
    }

    @EventHandler
    fun onTpaRequest(event: TpaRequestEvent) {
        val sender = event.sender
        val target = event.target

        if (sender.uniqueId == target.uniqueId)
            return sender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.tpa.error.request.yourself")))

        if (!magenta.tpaManager.createRequest(sender, target))
            return sender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.tpa.error.request.exist")))

        SchedulerMagenta.delayedTask(magenta, {
            magenta.tpaManager.killRequest(sender)
        }, Tick.tick().fromDuration(Duration.ofSeconds(magenta.config.getLong("tpa-accept-cancellation"))).toLong())
        PlayerBuilderAction
            .player(target)
            .sound(Sound.BLOCK_NOTE_BLOCK_PLING, 1.5F, 1.5F)
            .message(
                ModernText.miniModernText(
                    magenta.localeConfig.getMessage("magenta.command.tpa.success.request"), TagResolver.resolver(
                        Placeholder.component("player", sender.displayName()),
                        Placeholder.parsed(
                            "accept",
                            magenta.localeConfig.getMessage("magenta.command.tpa.success.request.component.accept")
                        ),
                        Placeholder.parsed(
                            "deny",
                            magenta.localeConfig.getMessage("magenta.command.tpa.success.request.component.deny")
                        ),)
                )
            )
    }
}