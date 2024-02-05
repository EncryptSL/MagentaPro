package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.scheduler.SchedulerMagenta
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Material
import org.bukkit.block.CreatureSpawner
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BlockStateMeta
import org.incendo.cloud.annotation.specifier.Range
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission


@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class SpawnerCmd(private val magenta: Magenta) {

    @Command("spawner set <type>")
    @Permission("magenta.spawner.set")
    fun onSpawnerSet(player: Player, @Argument(value = "type", suggestions = "mobs") entity: EntityType) {
        val block = player.getTargetBlock(null, 10)

        if (block.type != Material.SPAWNER)
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.spawner.error.not.spawner")))

        SchedulerMagenta.doSync(magenta) {
            val creatureSpawner: CreatureSpawner = block.state as CreatureSpawner
            creatureSpawner.spawnedType = entity
        }
        player.sendMessage(ModernText.miniModernText("<green>Spawner is now type ${entity.name}"))
    }

    @Command("spawner give <type> <amount> <player>")
    @Permission("magenta.spawner.give")
    fun onSpawnerGive(
        commandSender: CommandSender,
        @Argument(value = "type", suggestions = "mobs") entity: EntityType,
        @Argument(value = "amount") @Range(min = "1", max = "100") amount: Int,
        @Argument(value = "player", suggestions = "players") target: Player
    ) {
        SchedulerMagenta.doSync(magenta) {
            val spawner = ItemStack(Material.SPAWNER, amount)
            val spawnerMeta = spawner.itemMeta
            val bsm: BlockStateMeta = spawner.itemMeta as BlockStateMeta
            val blockState = bsm.blockState

            spawnerMeta.displayName(ModernText.miniModernText("<red>SPAWNER <yellow>${entity.name}"))

            val creatureSpawner: CreatureSpawner = blockState as CreatureSpawner
            creatureSpawner.spawnedType to entity
            bsm.blockState = blockState
            creatureSpawner.update()
            spawner.setItemMeta(spawnerMeta)

            target.inventory.addItem(spawner)
        }

        target.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.spawner.success.given"), TagResolver.resolver(
            Placeholder.parsed("type", entity.name),
            Placeholder.parsed("amount", amount.toString())
        )))

        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.spawner.success.given.to"), TagResolver.resolver(
            Placeholder.parsed("type", entity.name),
            Placeholder.parsed("amount", amount.toString()),
            Placeholder.parsed("player", target.name)
        )))
    }

}