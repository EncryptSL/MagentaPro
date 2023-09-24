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

    private val request = HashMap<UUID, UUID>()

    fun createRequest(sender: Player, target: Player) : Boolean {
        if (request.containsKey(sender.uniqueId)) return false

        request[target.uniqueId] = sender.uniqueId
        return true
    }

    fun acceptRequest(player: Player): Boolean {
        if (!request.containsKey(player.uniqueId)) return false

        val uuid = request[player.uniqueId] ?: return false
        val target = Bukkit.getPlayer(uuid) ?: return false


        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.tpa.success.request.accepted")))
        target.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.tpa.success.request.accepted.to"), TagResolver.resolver(
            Placeholder.parsed("player", target.name)
        )))
        target.playSound(target, Sound.BLOCK_NOTE_BLOCK_PLING, 1.5F, 1.5F)
        target.teleport(player)

        return true
    }

    fun denyRequest(player: Player) {
        if (!request.containsKey(player.uniqueId)) {
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.tpa.error.request.not.exist")))
        } else {
            val uuid = request[player.uniqueId] ?: return
            val expire = Bukkit.getPlayer(uuid)
            expire?.playSound(expire, Sound.BLOCK_NOTE_BLOCK_BASS, 1.5F, 1.5F)
            expire?.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.tpa.error.denied.to")))
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.tpa.error.denied")))
            request.remove(player.uniqueId)
        }
    }

    fun killRequest(player: Player) {
        if (request.containsKey(player.uniqueId)) {
            val uuid = request[player.uniqueId] ?: return
            val expire = Bukkit.getPlayer(uuid)
            expire?.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.tpa.error.request.expired")))

            request.remove(player.uniqueId)
        }
    }

}