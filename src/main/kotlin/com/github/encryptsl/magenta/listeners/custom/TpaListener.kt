package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.teleport.TpaAcceptEvent
import com.github.encryptsl.magenta.api.events.teleport.TpaDenyEvent
import com.github.encryptsl.magenta.api.events.teleport.TpaRequestEvent
import com.github.encryptsl.magenta.common.Permissions
import com.github.encryptsl.magenta.common.PlayerBuilderAction
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

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
        val user = magenta.user.getUser(sender.uniqueId)

        if (sender.uniqueId == target.uniqueId)
            return sender.sendMessage(magenta.locale.translation("magenta.command.tpa.error.request.yourself"))

        if (user.hasDelay("commands.tpa") && !sender.hasPermission(Permissions.KIT_DELAY_EXEMPT))
            return magenta.commandHelper.delayMessage(sender, "magenta.command.tpa.error.request.delay", user.getRemainingCooldown("commands.tpa"))

        if (!magenta.tpaManager.createRequest(sender, target))
            return sender.sendMessage(magenta.locale.translation("magenta.command.tpa.error.request.exist"))

        PlayerBuilderAction
            .player(target)
            .sound("block.note_block.pling", 1.5F, 1.5F)
            .message(magenta.locale.translation("magenta.command.tpa.success.request", TagResolver.resolver(
                Placeholder.component("player", sender.displayName()),
                Placeholder.component("accept", magenta.locale.translation("magenta.command.tpa.success.request.component.accept")),
                Placeholder.component("deny", magenta.locale.translation("magenta.command.tpa.success.request.component.deny"))
            )))
    }
}