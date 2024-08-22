package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.Permissions
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByBlockEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.entity.EntityTargetEvent

class VanishListener(private val magenta: Magenta) : Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    fun onVanishEntityTarget(event: EntityTargetEvent) {
        if (event.target is Player && magenta.user.getUser(event.entity.uniqueId).isVanished()) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onVanishPlayerDamageByBlock(event: EntityDamageByBlockEvent) {
        if (event.entity is Player && magenta.user.getUser(event.entity.uniqueId).isVanished() && !event.entity.hasPermission(Permissions.VANISH_BLOCK_DAMAGE_BYPASS)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onVanishPlayerDamage(event: EntityDamageByEntityEvent) {
        if (event.damager is Player && magenta.user.getUser(event.entity.uniqueId).isVanished() && !event.entity.hasPermission(Permissions.VANISH_PLAYER_DAMAGE_BYPASS)) {
            event.isCancelled = true
        } else if (event.entity is Player && magenta.user.getUser(event.entity.uniqueId).isVanished() && !event.entity.hasPermission(Permissions.VANISH_PLAYER_DAMAGE_BYPASS)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onVanishPlayerPickUpItem(event: EntityPickupItemEvent) {
        val entity: Entity = event.entity
        if (entity is Player) {
            val isVanished = magenta.user.getUser(entity.uniqueId).isVanished()
            if (isVanished && !entity.hasPermission(Permissions.VANISH_PICK_UP_ITEM_BYPASS)) {
                event.isCancelled = true
            }
        }
    }

}