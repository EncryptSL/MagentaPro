package com.github.encryptsl.magenta.common

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Player
import java.util.*

class TpaRequestManager(private val magenta: Magenta) {

    private val request: MutableMap<UUID, UUID> = HashMap()

    fun createRequest(sender: Player, target: Player) : Boolean {
        if (request.containsKey(sender.uniqueId))
            return false

        request[target.uniqueId] = sender.uniqueId
        return true
    }

    fun acceptRequest(player: Player) {
        if (!request.containsKey(player.uniqueId)) {
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.tpa.error.request.not.exist")))
        } else {
            val target = Bukkit.getPlayer(request[player.uniqueId].toString())

            if (target != null) {
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
                magenta.schedulerMagenta.runTask(magenta) {
                    target.teleport(player)
                }
                request.remove(player.uniqueId)
            } else {
                player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.tpa.error.accept")))
            }
        }
    }

    fun denyRequest(player: Player) {
        if (!request.containsKey(player.uniqueId)) {
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.tpa.error.request.not.exist")))
        } else {
            val sender = Bukkit.getPlayer(UUID.fromString(request[player.uniqueId].toString()))
            sender?.playSound(sender, Sound.BLOCK_NOTE_BLOCK_BASS, 1.5F, 1.5F)
            sender?.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.tpa.error.denied.to")))
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.tpa.error.denied")))
            request.remove(player.uniqueId)
        }
    }

    fun killRequest(player: Player) {
        if (request.containsKey(player.uniqueId)) {
            val expire = Bukkit.getPlayer(request[player.uniqueId].toString())
            expire?.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.tpa.error.request.expired")))
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.tpa.error.request.expired.to"),
                Placeholder.parsed("player", Bukkit.getOfflinePlayer(UUID.fromString(request[player.uniqueId].toString())).name.toString())
            ))
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 1.5F, 1.5F)
            request.remove(player.uniqueId)
        }
    }

}