package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.account.PlayerAccount
import com.github.encryptsl.magenta.api.events.jail.JailPlayerEvent
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

class EntityAttackListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onEntityAttack(event: EntityDamageByEntityEvent) {
        val entity = event.entity
        if (entity is Player) {
            val account = PlayerAccount(magenta, entity.uniqueId)
            if (event.cause  == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                val playerJailPlayerEvent = JailPlayerEvent(entity, null)
                playerJailPlayerEvent.callEvent()
                if(playerJailPlayerEvent.isCancelled) {
                    event.isCancelled = true
                }
                if (account.isJailed() || account.jailManager.hasPunish()) {
                    event.isCancelled = true
                }
            }
        }
        if (entity is Mob) {
            if (event.damager is Player) {
                val player = event.damager as Player
                if (event.cause  == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                    val playerJailPlayerEvent = JailPlayerEvent(player, "útočit")
                    playerJailPlayerEvent.callEvent()
                    if(playerJailPlayerEvent.isCancelled) {
                        event.isCancelled = true
                    }
                }
            }
        }
    }


}