package com.github.encryptsl.magenta.common

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.account.models.UserAccountImpl
import com.github.encryptsl.magenta.api.level.LevelFormula
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

    fun doVanish(user: UserAccountImpl) {
        for (players in Bukkit.getOnlinePlayers()) {
            if (user.getPlayer()?.hasPermission(Permissions.VANISH_EXEMPT) == true) continue
            if (user.isVanished()) {
                user.getPlayer()?.let { players.showPlayer(magenta, it) }
                user.getPlayer()?.let { players.listPlayer(it) }
                user.set("vanished", false)
            } else {
                user.getPlayer()?.let { players.hidePlayer(magenta, it) }
                user.getPlayer()?.let { players.unlistPlayer(it) }
                user.set("vanished", true)
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
                    magenta.locale.translation("magenta.command.fly.success.deactivated.to",
                        Placeholder.parsed("player", player.name)
                    )
                )
            }
            false -> {
                player.allowFlight = true
                magenta.user.getUser(player.uniqueId).set("flying", true)
                player.sendMessage(magenta.locale.translation("magenta.command.fly.success.activated"))
                commandSender?.sendMessage(
                    magenta.locale.translation("magenta.command.fly.success.activated.to", Placeholder.parsed("player", player.name))
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

    fun isVanished(boolean: Boolean): String {
        return if (boolean) "viditelný" else "skryt"
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
        val needToLevelUp = LevelFormula.experienceFormula(level)
        val percentageProgress = LevelFormula.levelProgress(level, currentExp)
        val progressBar = LevelFormula.getProgressBar(
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