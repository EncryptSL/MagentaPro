package com.github.encryptsl.magenta.common

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.account.PlayerAccount
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.inventory.meta.Damageable
import java.time.Duration

class CommandHelper(private val magenta: Magenta) {

    fun delayMessage(sender: Player, message: String, duration: Duration) {
        sender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage(message),
            Placeholder.parsed("delay", duration.toSeconds().toString())
        ))
    }


    fun teleportAll(sender: Player, players: MutableCollection<out Player>) {
        players.forEach {
            it.teleportAsync(sender.location)
        }
    }

    fun teleportOffline(sender: Player, target: OfflinePlayer) {
        val playerAccount = PlayerAccount(magenta, target.uniqueId)

        sender.teleport(playerAccount.getLastLocation(), PlayerTeleportEvent.TeleportCause.COMMAND)
    }

    fun toggleSocialSpy(player: Player, boolean: Boolean) {
        val account = PlayerAccount(magenta, player.uniqueId)
        account.set("socialspy", boolean)
    }

    fun changeDisplayName(player: Player, displayName: String) {
        val account = PlayerAccount(magenta, player.uniqueId)
        account.set("displayname", displayName)
        player.displayName(ModernText.miniModernText(account.getAccount().getString("displayname").toString()))
        player.playerListName(ModernText.miniModernText(account.getAccount().getString("displayname").toString()))
    }

    fun allowFly(commandSender: CommandSender?, player: Player) {
        if (player.allowFlight) {
            player.allowFlight = false
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.fly.success.deactivated")))
            commandSender?.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.fly.success.deactivated.to"), TagResolver.resolver(
                Placeholder.parsed("player", player.name))))
        } else {
            player.allowFlight = true
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.fly.success.activated")))
            commandSender?.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.fly.success.activated.to"), TagResolver.resolver(Placeholder.parsed("player", player.name))))

        }
    }

    fun repairItem(player: Player) {
        val inventory = player.inventory
        val item = inventory.itemInMainHand
        val itemMeta = item.itemMeta
        if (itemMeta is Damageable) {
            if (!itemMeta.hasDamage()) return
            itemMeta.damage = 0
            item.setItemMeta(itemMeta)
            player.updateInventory()
        }
        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.repair.success.item")))
    }

    fun repairItems(player: Player) {
        val inventory = player.inventory
        inventory.forEach { item ->
            val itemMeta = item?.itemMeta
            if (itemMeta is Damageable) {
                itemMeta.damage = 0
                item.setItemMeta(itemMeta)
                player.updateInventory()
            }
        }
    }

}