package com.github.encryptsl.magenta.common.hook.nuvotifier

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.account.PlayerAccount
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable


object VoteHelper {

    @JvmStatic
    fun saveOfflineReward(magenta: Magenta, offlinePlayer: OfflinePlayer, rewards: MutableList<String>) {
        val playerAccount = PlayerAccount(magenta, offlinePlayer.uniqueId)
        playerAccount.set("votifier.rewards", rewards)
        magenta.logger.info("Player ${offlinePlayer.name ?: offlinePlayer.uniqueId} vote and rewards are saved because he is offline !")
    }

    fun setVotePartyVote(magenta: Magenta, vote: Int) {
        magenta.config.set("votifier.voteparty.current_votes", vote)
        magenta.saveConfig()
    }
    @JvmStatic
    fun startVoteParty(
        magenta: Magenta,
        broadcastMessage: String,
        endPartyMessage: String,
        commands: MutableList<String>,
        countdown: Int
    ) {
        object : BukkitRunnable() {
            val timer = countdown
            override fun run() {
                timer.minus(1)
                broadcast(broadcastMessage, countdown)
                if (timer == 0) {
                    Bukkit.getOnlinePlayers().forEach { player: Player ->
                        giveRewards(commands, player.name)
                    }
                    broadcast(endPartyMessage)
                    setVotePartyVote(magenta, 0)
                    cancel()
                }
            }
        }.runTaskTimerAsynchronously(magenta, 20, 20)
    }

    @JvmStatic
    fun playSoundForAll(sound: Sound, volume: Float, pitch: Float) {
        Bukkit.getOnlinePlayers().forEach { player ->
            player.playSound(player, sound, volume, pitch)
        }
    }

    @JvmStatic
    fun broadcast(string: String, username: String, serviceName: String) {
        Bukkit.broadcast(ModernText.miniModernText(string, TagResolver.resolver(
            Placeholder.parsed("player", username),
            Placeholder.parsed("service", serviceName)
        )))
    }

    @JvmStatic
    fun broadcast(string: String, countdown: Int) {
        Bukkit.broadcast(ModernText.miniModernText(string, TagResolver.resolver(
            Placeholder.parsed("seconds", countdown.toString()),
        )))
    }

    @JvmStatic
    fun broadcast(string: String) {
        Bukkit.broadcast(ModernText.miniModernText(string))
    }

    @JvmStatic
    fun giveRewards(commands: MutableList<String>, username: String) {
        commands.forEach { command ->
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", username))
        }
    }

}