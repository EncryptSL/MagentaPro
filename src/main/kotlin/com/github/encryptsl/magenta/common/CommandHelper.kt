package com.github.encryptsl.magenta.common

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.PlayerAccount
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.inventory.meta.Damageable

class CommandHelper(private val magenta: Magenta) {

    fun teleportAll(sender: Player, players: MutableCollection<out Player>) {
        players.forEach {
            it.teleportAsync(sender.location)
        }
    }

    fun teleportOffline(sender: Player, target: OfflinePlayer) {
        val playerAccount = PlayerAccount(magenta, target.uniqueId)
        val world = playerAccount.getAccount().getString("lastlocation.world-name").toString()
        val x = playerAccount.getAccount().getDouble("lastlocation.x")
        val y = playerAccount.getAccount().getDouble("lastlocation.y")
        val z = playerAccount.getAccount().getDouble("lastlocation.z")
        val pitch = playerAccount.getAccount().getDouble("lastlocation.pitch")
        val yaw = playerAccount.getAccount().getDouble("lastlocation.yaw")

        sender.teleport(Location(Bukkit.getWorld(world), x, y, z, pitch.toFloat(), yaw.toFloat()), PlayerTeleportEvent.TeleportCause.COMMAND)
    }

    fun toggleSocialSpy(player: Player, boolean: Boolean) {
        val account = PlayerAccount(magenta, player.uniqueId)
        account.getAccount().set("socialspy", boolean)
        account.save()
    }

    fun changeDisplayName(player: Player, displayName: String) {
        val account = PlayerAccount(magenta, player.uniqueId)
        account.getAccount().set("displayname", displayName)
        account.save()
        player.displayName(ModernText.miniModernText(account.getAccount().getString("displayname").toString()))
        player.playerListName(ModernText.miniModernText(account.getAccount().getString("displayname").toString()))
    }

    fun allowFly(commandSender: CommandSender?, player: Player) {
        if (player.isFlying) {
            player.allowFlight = false
            player.isFlying = false
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.fly.success.deactivated")))
            commandSender?.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.fly.success.deactivated.to"), TagResolver.resolver(
                Placeholder.parsed("player", player.name))))
        } else {
            player.allowFlight = true
            player.isFlying = true
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.fly.success.activated")))
            commandSender?.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.fly.success.activated.to"), TagResolver.resolver(Placeholder.parsed("player", player.name))))

        }
    }

    fun repairItem(player: Player) {
        val inventory = player.inventory
        val item = inventory.itemInMainHand
        val itemMeta = item.itemMeta
        if (itemMeta is Damageable) {
            val dmg = itemMeta
            if (!dmg.hasDamage()) return
            dmg.damage = 0
            item.setItemMeta(dmg)
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