package com.github.encryptsl.magenta.common.model

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.PlayerBuilderAction
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.set
import kotlin.let
import kotlin.toString

class TpaManager(private val magenta: Magenta) {

    private val request: MutableMap<UUID, TpaRequest> = HashMap()

    fun createRequest(sender: Player, target: Player) : Boolean {
        if (request.containsKey(sender.uniqueId)) return false

        request[target.uniqueId] = TpaRequest(sender.uniqueId, target.uniqueId)
        return true
    }

    fun acceptRequest(player: Player) {
        if (!request.containsKey(player.uniqueId)) {
            return player.sendMessage(magenta.locale.translation("magenta.command.tpa.error.request.not.exist"))
        }

        val target = request[player.uniqueId]?.to?.let { Bukkit.getPlayer(it) }
            ?: return player.sendMessage(magenta.locale.translation("magenta.command.tpa.error.accept"))

        player.sendMessage(magenta.locale.translation("magenta.command.tpa.success.request.accepted"))
        PlayerBuilderAction
            .player(target)
            .message(magenta.locale.translation("magenta.command.tpa.success.request.accepted.to",
                TagResolver.resolver(
                    Placeholder.parsed("player", target.name)
                )
            )).sound("block.note_block.pling", 1.5F, 1.5F)
        target.teleport(player)
        request.remove(player.uniqueId)
    }

    fun denyRequest(player: Player) {
        if (!request.containsKey(player.uniqueId)) {
            return player.sendMessage(magenta.locale.translation("magenta.command.tpa.error.request.not.exist"))
        }

        val sender = request[player.uniqueId]?.from?.let { Bukkit.getPlayer(it) }
        sender?.playSound(sender, Sound.BLOCK_NOTE_BLOCK_BASS, 1.5F, 1.5F)
        sender?.sendMessage(magenta.locale.translation("magenta.command.tpa.error.denied.to"))
        player.sendMessage(magenta.locale.translation("magenta.command.tpa.error.denied"))
        request.remove(player.uniqueId)
    }

    fun killRequest(player: Player) {
        if (!request.containsKey(player.uniqueId)) return

        val expire = request[player.uniqueId]?.to?.let { Bukkit.getPlayer(it) }
        expire?.sendMessage(magenta.locale.translation("magenta.command.tpa.error.request.expired"))
        PlayerBuilderAction
            .player(player)
            .message(magenta.locale.translation("magenta.command.tpa.error.request.expired.to",
                Placeholder.parsed("player", Bukkit.getOfflinePlayer(UUID.fromString(request[player.uniqueId].toString())).name.toString())
            )).sound("block.note_block.bass", 1.5F, 1.5F)
        request.remove(player.uniqueId)
    }

    data class TpaRequest(val from: UUID, val to: UUID)
}