package com.github.encryptsl.magenta.common.hook.nuvotifier

import club.minnced.discord.webhook.send.WebhookEmbed
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.account.UserAccount
import com.github.encryptsl.magenta.api.config.locale.Locale
import com.github.encryptsl.magenta.api.events.vote.VotePartyEvent
import com.github.encryptsl.magenta.api.events.vote.VotePartyPlayerWinner
import com.github.encryptsl.magenta.api.scheduler.SchedulerMagenta
import com.github.encryptsl.magenta.common.extensions.datetime
import com.github.encryptsl.magenta.common.extensions.toMinotarAvatar
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable


object VoteHelper {

    @JvmStatic
    fun saveOfflineReward(magenta: Magenta, offlinePlayer: OfflinePlayer, rewards: MutableList<String>, expressionFormula: String = "") {
        rewards.replaceAll { s -> s
            .replace("{player}", offlinePlayer.name.toString())
            .replace("%player%", offlinePlayer.name.toString())
            .replace("{expression_reward}", expressionFormula)
        }

        val userAccount = UserAccount(magenta, offlinePlayer.uniqueId)
        userAccount.set("votifier.rewards", rewards)
        magenta.logger.info("Player ${offlinePlayer.name ?: offlinePlayer.uniqueId} vote and rewards are saved because he is offline !")
    }
    @JvmStatic
    fun startVoteParty(
        magenta: Magenta,
        commands: MutableList<String>,
        countdown: Int
    ) {
        object : BukkitRunnable() {
            var timer = countdown
            override fun run() {
                broadcastActionBar(
                    magenta.localeConfig.translation("magenta.votifier.voteparty.broadcast",
                        Placeholder.parsed("delay", timer.toString())
                    )
                )
                if (timer == 0) {
                    SchedulerMagenta.doSync(magenta) {
                        val players = Bukkit.getOnlinePlayers()
                        giveRewards(players, commands)
                        if (magenta.config.contains("votifier.voteparty.random")) {
                            val player = players.random()
                            giveRewards(magenta.config.getStringList("votifier.voteparty.random"), player.name)
                            magenta.voteParty.partyFinished(player.name)
                            magenta.pluginManager.callEvent(VotePartyPlayerWinner(player.name))
                            magenta.notification.addEmbed {
                                setTitle(WebhookEmbed.EmbedTitle("VoteParty", null))
                                setThumbnailUrl(player.uniqueId.toMinotarAvatar())
                                setColor(0xa730c2)
                                addField(WebhookEmbed.EmbedField(false, "Výherce", player.name))
                            }?.let { magenta.notification.client.send(it) }
                        }
                    }
                    broadcast(magenta.localeConfig.translation("magenta.votifier.voteparty.success"))
                    cancel()
                }
                timer--
            }
        }.runTaskTimerAsynchronously(magenta, 20, 20)
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
        for (it in players) {
            giveRewards(commands, it.name)
            Bukkit.getPluginManager().callEvent(VotePartyEvent(it, Bukkit.getOnlinePlayers().size, datetime()))
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