package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.jail.JailPlayerEvent
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
            if (event.cause  == EntityDamageEvent.DamageCause.ENTITY_ATTACK || event.cause == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK || event.cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
                val jailPlayerEvent = JailPlayerEvent(entity, "útočit")
                if (jailPlayerEvent.isCancelled) {
                    event.isCancelled = true
                }
            }
            return
        }
    }

}