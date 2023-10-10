package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ModernText
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

class PlayerInteractionListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onPlayerInteraction(event: PlayerInteractEvent) {
        val player = event.player
        magenta.afk.addTime(player, magenta.config.getLong("auto-afk"))

        if (player.inventory.itemInMainHand.hasItemMeta()) {
            if (event.action.isRightClick) {
                val itemInHand = player.inventory.itemInMainHand
                val itemMeta = itemInHand.itemMeta
                magenta.cItems.getConfig().getConfigurationSection("citems")?.getKeys(false)?.forEach {
                    if (itemMeta.hasDisplayName()) {
                        val sid = magenta.cItems.getConfig().getString("citems.$it.sid").toString()
                        val item = magenta.cItems.getConfig().getString("citems.$it.name").toString().replace("<sid>", sid)
                        if (itemMeta.displayName() == ModernText.miniModernText(item)) {
                            val command = magenta.cItems.getConfig().getString("citems.$it.command").toString()
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), magenta.stringUtils.magentaPlaceholders(command, player))
                            player.inventory.remove(itemInHand)
                        }
                    }
                }
            }
        }
    }

}