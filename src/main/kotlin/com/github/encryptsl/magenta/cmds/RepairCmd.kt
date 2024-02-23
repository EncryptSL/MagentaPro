package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.CommandHelper
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission
import java.time.Duration

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class RepairCmd(private val magenta: Magenta) {

    private val commandHelper = CommandHelper(magenta)

    @Command("repair")
    @Permission("magenta.repair.item")
    fun onRepair(player: Player) {
        val inventory = player.inventory
        val user = magenta.user.getUser(player.uniqueId)
        val delay = magenta.config.getLong("repair-cooldown")

        if (inventory.itemInMainHand.type.isEmpty || inventory.itemInMainHand.type.isAir || inventory.itemInMainHand.isEmpty)
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.repair.error.empty.hand")))

        val timeLeft = user.getRemainingCooldown("repair")
        if (user.hasDelay("repair") && !player.hasPermission("magenta.repair.delay.exempt"))
            return commandHelper.delayMessage(player, "magenta.command.repair.error.delay", timeLeft)

        if (delay != 0L && delay != -1L || !player.hasPermission("magenta.repair.delay.exempt")) {
            user.setDelay(Duration.ofSeconds(delay), "repair")
        }

        commandHelper.repairItem(player)
    }

    @Command("fixall|repair all")
    @Permission("magenta.repair.all")
    fun onRepairAll(player: Player) {
        val user = magenta.user.getUser(player.uniqueId)
        val delay = magenta.config.getLong("repair-cooldown")

        val inventory = player.inventory
        if (inventory.isEmpty)
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.repair.error.empty.inventory")))

        val timeLeft = user.getRemainingCooldown("repair")
        if (user.hasDelay("repair") && !player.hasPermission("magenta.repair.delay.exempt"))
            return commandHelper.delayMessage(player, "magenta.command.repair.error.delay", timeLeft)

        if (delay != 0L && delay != -1L || !player.hasPermission("magenta.repair.delay.exempt")) {
            user.setDelay(Duration.ofSeconds(delay), "repair")
        }

        commandHelper.repairItems(player)

        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.repair.success.all")))
    }

    @Command("fixall|repair all <target>")
    @Permission("magenta.repair.all.other")
    fun onRepairAllProxy(commandSender: CommandSender,  @Argument(value = "target", suggestions = "players") target: Player) {
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