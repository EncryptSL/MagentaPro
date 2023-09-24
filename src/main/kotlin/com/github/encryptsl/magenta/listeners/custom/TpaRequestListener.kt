package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.teleport.TpaRequestEvent
import com.github.encryptsl.magenta.common.TpaRequestManager
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class TpaRequestListener(private val magenta: Magenta) : Listener {

    private val tpaRequestManager = TpaRequestManager(magenta)

    @EventHandler
    fun onTpaRequest(event: TpaRequestEvent) {
        val sender = event.sender
        val target = event.target

        if (!tpaRequestManager.createRequest(sender, target))
            return sender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.tpa.error.request.exist")))

        magenta.schedulerMagenta.delayedTask(magenta, {
           tpaRequestManager.killRequest(sender)
        }, magenta.config.getLong("tpa-accept-cancellation"))

        target.playSound(target, Sound.BLOCK_NOTE_BLOCK_PLING, 1.5F, 1.5F)
        target.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.tpa.success.request"), TagResolver.resolver(
            Placeholder.component("player", sender.displayName()),
            Placeholder.parsed("accept", magenta.localeConfig.getMessage("magenta.command.tpa.success.request.component.accept")),
            Placeholder.parsed("deny", magenta.localeConfig.getMessage("magenta.command.tpa.success.request.component.deny")),
        )))
    }


}