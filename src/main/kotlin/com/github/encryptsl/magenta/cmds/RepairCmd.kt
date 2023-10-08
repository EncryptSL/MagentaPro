package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.*
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.account.PlayerAccount
import com.github.encryptsl.magenta.common.CommandHelper
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.time.Duration

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class RepairCmd(private val magenta: Magenta) {

    private val commandHelper = CommandHelper(magenta)

    @CommandMethod("repair")
    @CommandPermission("magenta.repair.item")
    fun onRepair(player: Player) {
        val inventory = player.inventory
        val delay = magenta.config.getLong("repair-cooldown")
        val playerAccount = PlayerAccount(magenta, player.uniqueId)

        if (inventory.itemInMainHand.type.isEmpty || inventory.itemInMainHand.type.isAir || inventory.itemInMainHand.isEmpty)
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.repair.error.empty.hand")))

        val timeLeft = playerAccount.cooldownManager.getRemainingDelay("repair")
        if (!playerAccount.cooldownManager.hasDelay("repair")) {
            if (delay != 0L && delay != -1L || !player.hasPermission("magenta.repair.delay.exempt")) {
                playerAccount.cooldownManager.setDelay(Duration.ofSeconds(delay), "repair")
            }

            commandHelper.repairItem(player)
        } else {
            commandHelper.delayMessage(player, "magenta.command.repair.error.delay", timeLeft)
        }
    }

    @ProxiedBy("fixall")
    @CommandMethod("repair all [target]")
    @CommandPermission("magenta.repair.all")
    fun onRepairAll(commandSender: CommandSender, @Argument(value = "target", suggestions = "players") target: Player?) {
        if (commandSender is Player) {
            if (target == null) {
                val inventory = commandSender.inventory
                if (inventory.isEmpty)
                    return commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.repair.error.empty.inventory")))

                commandHelper.repairItems(commandSender)
                commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.repair.success.all")))
                return
            }

            if (!target.hasPermission("magenta.repair.others"))
                return commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("")))

            val inventory = target.inventory
            if (inventory.isEmpty)
                return commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.repair.error.empty.inventory")))

            commandHelper.repairItems(target)

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
                if (!target.hasPermission("magenta.repair.other"))
                    return commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("")))

                val inventory = target.inventory
                if (inventory.isEmpty)
                    return commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.repair.error.empty.inventory")))

                commandHelper.repairItems(target)

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