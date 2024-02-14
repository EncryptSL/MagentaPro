package com.github.encryptsl.magenta.common

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.scheduler.SchedulerMagenta
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Player
import java.util.*

class TpaManager(private val magenta: Magenta) {

    private val request: MutableMap<UUID, TpaRequest> = HashMap()

    fun createRequest(sender: Player, target: Player) : Boolean {
        if (request.containsKey(sender.uniqueId))
            return false

        request[target.uniqueId] = TpaRequest(sender.uniqueId, target.uniqueId)
        return true
    }

    fun acceptRequest(player: Player) {
        if (!request.containsKey(player.uniqueId)) {
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.tpa.error.request.not.exist")))
        } else {
            val target = Bukkit.getPlayer(UUID.fromString(request[player.uniqueId]?.to.toString()))
                ?: return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.tpa.error.accept")))

            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.tpa.success.request.accepted")))
            target.sendMessage(
                ModernText.miniModernText(
                    magenta.localeConfig.getMessage("magenta.command.tpa.success.request.accepted.to"),
                    TagResolver.resolver(
                        Placeholder.parsed("player", target.name)
                    )
                )
            )
            target.playSound(target, Sound.BLOCK_NOTE_BLOCK_PLING, 1.5F, 1.5F)
            SchedulerMagenta.doSync(magenta) {
                target.teleport(player)
            }
            request.remove(player.uniqueId)
        }
    }

    fun denyRequest(player: Player) {
        if (!request.containsKey(player.uniqueId)) {
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.tpa.error.request.not.exist")))
        } else {
            val sender = request[player.uniqueId]?.from?.let { Bukkit.getPlayer(it) }
            sender?.playSound(sender, Sound.BLOCK_NOTE_BLOCK_BASS, 1.5F, 1.5F)
            sender?.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.tpa.error.denied.to")))
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.tpa.error.denied")))
            request.remove(player.uniqueId)
        }
    }

    fun killRequest(player: Player) {
        if (request.containsKey(player.uniqueId)) {
            val expire = request[player.uniqueId]?.to?.let { Bukkit.getPlayer(it) }
            expire?.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.tpa.error.request.expired")))
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.tpa.error.request.expired.to"),
                Placeholder.parsed("player", Bukkit.getOfflinePlayer(UUID.fromString(request[player.uniqueId].toString())).name.toString())
            ))
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 1.5F, 1.5F)
            request.remove(player.uniqueId)
        }
    }

}