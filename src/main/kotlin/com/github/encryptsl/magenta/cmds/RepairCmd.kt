package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.*
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.command.CommandSender
import org.bukkit.entity.Damageable
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class RepairCmd(private val magenta: Magenta) {

    @CommandMethod("repair")
    @CommandPermission("magenta.repair.item")
    fun onRepair(player: Player) {
        val inventory = player.inventory
        val itemMeta = inventory.itemInMainHand.itemMeta
        val dmg = itemMeta as Damageable
        if (inventory.itemInMainHand.type.isEmpty || inventory.itemInMainHand.type.isAir || inventory.itemInMainHand.isEmpty)
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.repair.error.empty.hand")))

        dmg.damage(0.0)
        inventory.itemInMainHand.setItemMeta(itemMeta)
        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.repair.success.item")))
    }

    @ProxiedBy("fixall")
    @CommandMethod("repair all [target]")
    @CommandPermission("magenta.repair.all")
    fun onRepairAll(commandSender: CommandSender, @Argument(value = "target", suggestions = "players") target: Player?) {
        if (commandSender is Player) {
            if (target == null) {
                val inventory = commandSender.inventory
                if (inventory.contents.isEmpty())
                    return commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.repair.error.empty.inventory")))

                inventory.contents.forEach { item ->
                    val itemMeta = item?.itemMeta
                    val dmg = itemMeta as Damageable
                    dmg.damage(0.0)
                    item.setItemMeta(itemMeta)
                }

                commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.repair.success.all")))
                return
            }

            val inventory = target.inventory
            if (inventory.contents.isEmpty())
                return commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.repair.error.empty.inventory")))

            inventory.contents.forEach { item ->
                val itemMeta = item?.itemMeta
                val dmg = itemMeta as Damageable
                dmg.damage(0.0)
                item.setItemMeta(itemMeta)
            }

            target.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.repair.success.all")))
            commandSender.sendMessage(
                ModernText.miniModernText(
                    magenta.localeConfig.getMessage("magenta.command.repair.success.all.to"), TagResolver.resolver(
                        Placeholder.parsed("player", target.name)
                    )
                )
            )

        } else {
            if (target != null) {
                val inventory = target.inventory
                if (inventory.contents.isEmpty())
                    return commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.repair.error.empty.inventory")))

                inventory.contents.forEach { item ->
                    val itemMeta = item?.itemMeta
                    val dmg = itemMeta as Damageable
                    dmg.damage(0.0)
                    item.setItemMeta(itemMeta)
                }

                target.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.repair.success.all")))
                commandSender.sendMessage(
                    ModernText.miniModernText(
                        magenta.localeConfig.getMessage("magenta.command.repair.success.all.to"), TagResolver.resolver(
                            Placeholder.parsed("player", target.name)
                        )
                    )
                )
            }
        }
    }



}