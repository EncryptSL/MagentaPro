package com.github.encryptsl.magenta.common.hook.nuvotifier

import com.github.encryptsl.kmono.lib.api.config.locale.Locale
import com.github.encryptsl.kmono.lib.extensions.datetime
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.vote.VotePartyEvent
import com.github.encryptsl.magenta.common.tasks.VotePartyTask
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player


object VoteHelper {

    @JvmStatic
    fun saveOfflineReward(magenta: Magenta, offlinePlayer: OfflinePlayer, rewards: MutableList<String>, expressionFormula: String = "") {
        rewards.replaceAll { s -> s
            .replace("{player}", offlinePlayer.name.toString())
            .replace("%player%", offlinePlayer.name.toString())
            .replace("{expression_reward}", expressionFormula)
        }

        val userAccount = magenta.user.getUser(offlinePlayer.uniqueId)
        userAccount.set("votifier.rewards", rewards)
        magenta.logger.info("Player ${offlinePlayer.name ?: offlinePlayer.uniqueId} vote and rewards are saved because he is offline !")
    }
    @JvmStatic
    fun startVoteParty(
        magenta: Magenta,
        commands: MutableList<String>,
    ) {
        Magenta.scheduler.impl.runTimer(VotePartyTask(magenta, commands), 20, 20)
    }

    @JvmStatic
    fun playSoundForAll(type: String, volume: Float, pitch: Float) {
        Audience.audience(Bukkit.getOnlinePlayers())
            .playSound(
                net.kyori.adventure.sound.Sound.sound().type(Key.key(type))
                    .volume(volume)
                    .pitch(pitch)
                    .build()
            )
    }

    @JvmStatic
    fun broadcast(locale: Locale, key: String, username: String, serviceName: String) {
        Bukkit.broadcast(locale.translation(key, TagResolver.resolver(
            Placeholder.parsed("player", username),
            Placeholder.parsed("service", replaceService(serviceName, "_", "."))
        )))
    }

    @JvmStatic
    fun broadcastActionBar(component: Component) {
        Audience.audience(Bukkit.getOnlinePlayers())
            .sendActionBar(component)
    }

    @JvmStatic
    fun broadcast(component: Component) {
        Bukkit.broadcast(component)
    }

    @JvmStatic
    fun giveRewards(players: Collection<Player>, commands: MutableList<String>) {
        val iterator = players.iterator()
        while (iterator.hasNext()) {
            val player = iterator.next()
            giveRewards(commands, player.name)
            Bukkit.getPluginManager().callEvent(VotePartyEvent(player, players.size, datetime()))
        }
    }
    @JvmStatic
    fun giveRewards(commands: MutableList<String>, username: String, expressionFormula: String = "") {
        commands.replaceAll { s -> s
            .replace("{player}", username)
            .replace("%player%", username)
            .replace("{expression_reward}", expressionFormula)
        }

        for (command in commands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command)
        }
    }

    @JvmStatic
    fun replaceService(serviceName: String, oldValue: String, newValue: String): String {
        return serviceName.replace(oldValue, newValue)
    }

}