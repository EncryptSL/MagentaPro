package com.github.encryptsl.magenta.common.hook.mythicmobs

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.extensions.forEachIndexed
import com.github.encryptsl.magenta.common.utils.ModernText
import io.lumine.mythic.bukkit.MythicBukkit
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent
import io.lumine.mythic.bukkit.events.MythicMobDespawnEvent
import io.lumine.mythic.bukkit.events.MythicMobSpawnEvent
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import java.math.RoundingMode
import java.util.*

class MythicMobsListener(private val magenta: Magenta) : Listener {

    private val damageMap = HashMap<UUID, Double>()

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerDamageMythicMob(
        event: EntityDamageByEntityEvent
    ) {
        if (!MythicBukkit.inst().apiHelper.isMythicMob(event.entity)) return
        val entityName = MythicBukkit.inst().apiHelper.getMythicMobInstance(event.entity).type.internalName
        if (magenta.mmConfig.getConfig().contains("mythic_rewards.${entityName}")) {
            if (event.damager is Player) {
                damageMap[event.damager.uniqueId] = damageMap.getOrDefault(event.damager.uniqueId, 0.0).plus(event.damage)
            } else if (event.entity is Projectile) {
                val projectile: Projectile = event.entity as Projectile
                if (projectile.shooter is Player) {
                    damageMap[event.damager.uniqueId] = damageMap.getOrDefault(event.damager.uniqueId, 0.0).plus(event.damage)
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onMythicMobDeath(
        event: MythicMobDeathEvent
    ) {
        if (event.killer is Player) {
            val entityName = event.mobType.internalName
            if (magenta.mmConfig.getConfig().contains("mythic_rewards.$entityName")) {
                val positions = sortedByDamage()
                positions.forEach { (t, u) ->
                    for (message in magenta.mmConfig.getConfig().getStringList("mythic_rewards.$entityName.RewardMessage.message")) {
                        Bukkit.getPlayer(t)?.sendMessage(ModernText.miniModernTextCenter(
                                message
                                    .replace("{top_player_1}", nameByRank(1))
                                    .replace("{top_player_2}", nameByRank(2))
                                    .replace("{top_player_3}", nameByRank(3))
                                    .replace("{top_damage_1}", roundingDamage(1).toString())
                                    .replace("{top_damage_2}", roundingDamage(2).toString())
                                    .replace("{top_damage_3}", roundingDamage(3).toString())
                                    .replace("{personal_score}", roundingPersonalDamage(u).toString())
                        ))
                    }
                }

                if (magenta.mmConfig.getConfig().contains("mythic_rewards.$entityName.EventMessages.death")) {
                    sendBroadcast("mythic_rewards.$entityName.EventMessages.death", entityName)
                }

                if (magenta.mmConfig.getConfig().contains("mythic_rewards.$entityName.RewardCommands")) {
                    positions.filter { Bukkit.getPlayer(it.key) != null }.entries.forEachIndexed { index, entry ->
                        val onlineP = Bukkit.getPlayer(entry.key)!!
                        for (command in magenta.mmConfig.getConfig().getStringList("mythic_rewards.$entityName.RewardCommands.$index")) {
                            Bukkit.dispatchCommand(
                                Bukkit.getConsoleSender(),
                                command.replace("%player%", onlineP.name).replace("{player}", onlineP.name)
                            )
                        }
                    }
                }
                damageMap.clear()
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onSpawnMythicMob(event: MythicMobSpawnEvent) {
        val internalName = event.mobType.internalName
        if (event.isFromMythicSpawner) {
            if (!magenta.mmConfig.getConfig().contains("mythic_rewards.$internalName")) return
            if (!magenta.mmConfig.getConfig().contains("mythic_rewards.$internalName.EventMessages.spawn")) return

            sendBroadcast("mythic_rewards.$internalName.EventMessages.spawn", internalName)
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onDespawnMythicMob(event: MythicMobDespawnEvent) {
        val internalName = event.mobType.internalName
        if (!magenta.mmConfig.getConfig().contains("mythic_rewards.$internalName")) return

        if (!magenta.mmConfig.getConfig().contains("mythic_rewards.$internalName.EventMessages.despawn")) return

        sendBroadcast("mythic_rewards.$internalName.EventMessages.despawn", internalName)
    }

    private fun sendBroadcast(localeMessage: String, mobName: String) {
        Bukkit.broadcast(ModernText.miniModernText(
            magenta.mmConfig.getConfig().getString(localeMessage).toString(),
            Placeholder.parsed("mob", mobName))
        )
    }

    private fun roundingPersonalDamage(damage: Double): Double {
        return damage.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
    }
    private fun roundingDamage(position: Int): Double {
        return damageByRank(position).toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
    }

    private fun damageByRank(position: Int): Double {
        try {
            val topPlayer = sortedByDamage()
            return topPlayer.values.elementAt(position - 1)
        } catch (e : IndexOutOfBoundsException) {
            return 0.0
        }
    }

    private fun nameByRank(position: Int): String {
        try {
            val topPlayer = sortedByDamage()
            val uuid = topPlayer.keys.elementAt(position - 1)
            return Bukkit.getOfflinePlayer(uuid).name ?: "UNKNOWN"
        } catch (e: IndexOutOfBoundsException) {
            return "EMPTY"
        }
    }

    private fun sortedByDamage(): Map<UUID, Double> {
        return damageMap.toList().sortedByDescending { (_, damage) -> damage }.toMap()
    }

}