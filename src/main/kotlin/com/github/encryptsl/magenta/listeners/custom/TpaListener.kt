package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.account.PlayerAccount
import com.github.encryptsl.magenta.api.events.teleport.TpaAcceptEvent
import com.github.encryptsl.magenta.api.events.teleport.TpaDenyEvent
import com.github.encryptsl.magenta.api.events.teleport.TpaRequestEvent
import com.github.encryptsl.magenta.common.CommandHelper
import com.github.encryptsl.magenta.common.utils.ModernText
import io.papermc.paper.util.Tick
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.time.Duration

class TpaListener(private val magenta: Magenta) : Listener {

    private val commandHelper: CommandHelper by lazy { CommandHelper(magenta) }

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
        val delay = event.delay

        val playerAccount = PlayerAccount(magenta, sender.uniqueId)
        val timeLeft: Duration = playerAccount.cooldownManager.getRemainingDelay("tpa")

        if (sender.uniqueId == target.uniqueId)
            return sender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.tpa.error.request.yourself")))

        if (!playerAccount.cooldownManager.hasDelay("tpa")) {
            if (delay != 0L && delay != -1L) {
                if (!sender.hasPermission("magenta.tpa.delay.exempt")) {
                    playerAccount.cooldownManager.setDelay(Duration.ofSeconds(delay), "tpa")
                    playerAccount.save()
                }
            }

            if (!magenta.tpaManager.createRequest(sender, target))
                return sender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.tpa.error.request.exist")))


            magenta.schedulerMagenta.delayedTask(magenta, {
                magenta.tpaManager.killRequest(sender)
            }, Tick.tick().fromDuration(Duration.ofSeconds(magenta.config.getLong("tpa-accept-cancellation"))).toLong())
            target.playSound(target, Sound.BLOCK_NOTE_BLOCK_PLING, 1.5F, 1.5F)
            target.sendMessage(
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
                        ),
                    )
                )
            )
        } else {
            commandHelper.delayMessage(sender, "magenta.command.tpa.error.request.delay", timeLeft)
        }
    }
}