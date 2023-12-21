package com.github.encryptsl.magenta.common.hook.elitemobs

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.extensions.forEachIndexed
import com.github.encryptsl.magenta.common.utils.ModernText
import com.magmaguy.elitemobs.api.EliteMobDamagedByPlayerEvent
import com.magmaguy.elitemobs.api.EliteMobDeathEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import java.math.RoundingMode
import java.util.*

class EliteMobsListeners(private val magenta: Magenta) : Listener {

    private val damageMap = HashMap<UUID, Double>()

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerDamageEliteMob(event: EliteMobDamagedByPlayerEvent) {
        val eliteMobEntity = event.eliteMobEntity
        val player = event.player
        if (magenta.mmConfig.getConfig().contains("elitemobs_rewards.${eliteMobEntity.name}")) {
            if (event.entity is Projectile) {
                val projectile: Projectile = event.entity as Projectile
                if (projectile.shooter is Player) {
                    damageMap[player.uniqueId] = damageMap.getOrDefault(player.uniqueId, 0.0).plus(event.damage)
                }
                return
            }
            damageMap[player.uniqueId] = damageMap.getOrDefault(player.uniqueId, 0.0).plus(event.damage)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onMythicMobDeath(event: EliteMobDeathEvent) {
        if (event.entityDeathEvent.entity.killer is Player) {
            val entityName = event.eliteEntity.name
            if (magenta.mmConfig.getConfig().contains("elitemobs_rewards.$entityName")) {
                val positions = sortedByDamage()
                positions.forEach { (t, u) ->
                    for (message in magenta.mmConfig.getConfig().getStringList("elitemobs_rewards.$entityName.RewardMessage.message")) {
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

                if (magenta.mmConfig.getConfig().contains("elitemobs_rewards.$entityName.RewardCommands")) {
                    positions.filter { Bukkit.getPlayer(it.key) != null }.entries.forEachIndexed { index, entry ->
                        val onlineP = Bukkit.getPlayer(entry.key)!!
                        for (command in magenta.mmConfig.getConfig().getStringList("elitemobs_rewards.$entityName.RewardCommands.$index")) {
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