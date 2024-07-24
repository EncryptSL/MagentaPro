package com.github.encryptsl.magenta.common

import com.github.encryptsl.kmono.lib.extensions.experienceFormula
import com.github.encryptsl.kmono.lib.extensions.getProgressBar
import com.github.encryptsl.kmono.lib.extensions.levelProgress
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.account.models.UserAccountImpl
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.inventory.meta.Damageable
import java.time.Duration

class CommandHelper(private val magenta: Magenta) {

    fun delayMessage(sender: Player, message: String, duration: Duration) {
        sender.sendMessage(
                magenta.locale.translation(message,
                Placeholder.parsed("delay", duration.toSeconds().toString())
            )
        )
    }

    fun toggleVanish(user: UserAccountImpl) {
        for (onlinePlayers in Bukkit.getOnlinePlayers()) {
            when(user.isVanished()) {
                true -> {
                    user.set("vanished", false)
                    user.getPlayer()?.let {
                        onlinePlayers.showPlayer(magenta, it)
                        onlinePlayers.listPlayer(it)
                    }
                }
                false -> {
                    user.set("vanished", true)
                    if (onlinePlayers.hasPermission(Permissions.VANISH_EXEMPT)) return
                    user.getPlayer()?.let {
                        onlinePlayers.hidePlayer(magenta, it)
                        onlinePlayers.unlistPlayer(it)
                    }
                }
            }
        }
    }


    fun teleportAll(sender: Player, players: HashSet<Player>) {
        for (it in players) { it.teleportAsync(sender.location) }
        players.clear()
    }

    fun teleportOffline(sender: Player, target: OfflinePlayer) {
        val user = magenta.user.getUser(target.uniqueId)

        sender.teleport(user.getLastLocation(), PlayerTeleportEvent.TeleportCause.COMMAND)
    }

    fun toggleSocialSpy(player: Player, boolean: Boolean) {
        magenta.user.getUser(player.uniqueId).set("socialspy", boolean)
    }

    fun allowFly(commandSender: CommandSender?, player: Player) {

        when(player.allowFlight) {
            true -> {
                player.allowFlight = false
                magenta.user.getUser(player.uniqueId).set("flying", false)
                player.sendMessage(magenta.locale.translation("magenta.command.fly.success.deactivated"))
                commandSender?.sendMessage(
                    magenta.locale.translation("magenta.command.fly.success.deactivated.self.other",
                        Placeholder.parsed("player", player.name)
                    )
                )
            }
            false -> {
                player.allowFlight = true
                magenta.user.getUser(player.uniqueId).set("flying", true)
                player.sendMessage(magenta.locale.translation("magenta.command.fly.success.activated"))
                commandSender?.sendMessage(
                    magenta.locale.translation("magenta.command.fly.success.activated.self.other", Placeholder.parsed("player", player.name))
                )
            }
        }
    }

    fun repairItemFromHand(player: Player) {
        val inventory = player.inventory
        val item = inventory.itemInMainHand
        val itemMeta = item.itemMeta
        if (itemMeta is Damageable) {
            if (!itemMeta.hasDamage()) return
            itemMeta.damage = 0
            item.setItemMeta(itemMeta)
            player.updateInventory()
        }
        player.sendMessage(magenta.locale.translation("magenta.command.repair.success.item"))
    }

    fun repairItems(player: Player) {
        val inventory = player.inventory
        for (item in inventory) {
            val itemMeta = item.itemMeta
            if (itemMeta is Damageable) {
                itemMeta.damage = 0
                item.setItemMeta(itemMeta)
                player.updateInventory()
            }
        }
    }

    fun isVanished(user: UserAccountImpl): String {
        return if (user.isVanished()) "skryt" else "viditelný"
    }

    fun isMsgToggled(user: UserAccountImpl): Boolean {
      return if (user.getAccount().getBoolean("commands.toggle.msg")) {
            user.set("commands.toggle.msg", false)
            false
        } else {
            user.set("commands.toggle.msg", true)
            true
        }
    }

    fun showLevelProgress(commandSender: CommandSender, level: Int, currentExp: Int) {
        val needToLevelUp = experienceFormula(level)
        val percentageProgress = levelProgress(level, currentExp)
        val progressBar = getProgressBar(
            currentExp,
            needToLevelUp,
            magenta.config.getInt("level.progress_bar.total_bars", 20),
            magenta.config.getString("level.progress_bar.barSymbol").toString(),
            magenta.config.getString("level.progress_bar.completed_fields", "<green>").toString(),
            magenta.config.getString("level.progress_bar.not_completed_fields", "<gray>").toString(),
        )

        commandSender.sendMessage(
                magenta.locale.translation("magenta.command.level.success.your.level",
                Placeholder.parsed("level", level.toString())
            )
        )
        commandSender.sendMessage(
            magenta.locale.translation("magenta.command.level.success.score", TagResolver.resolver(
                    Placeholder.parsed("current_exp", currentExp.toString()),
                    Placeholder.parsed("exp_to_up", needToLevelUp.toString()),
                    Placeholder.parsed("percentage_progress", percentageProgress.toString())
            )
        ))
        commandSender.sendMessage(
            magenta.locale.translation("magenta.command.level.success.progressbar",
                Placeholder.parsed("progress_bar", progressBar)
            )
        )
    }
}