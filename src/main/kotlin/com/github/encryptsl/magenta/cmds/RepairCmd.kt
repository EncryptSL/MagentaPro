package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.commands.AnnotationFeatures
import com.github.encryptsl.magenta.common.Permissions
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.*
import org.incendo.cloud.paper.PaperCommandManager
import java.time.Duration

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class RepairCmd(private val magenta: Magenta) : AnnotationFeatures {

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: PaperCommandManager<CommandSender>
    ) {
        annotationParser.parse(this)
    }

    @Command("repair")
    @Permission("magenta.repair.item")
    @CommandDescription("This command repair item in your hand")
    fun onRepair(player: Player) {
        val inventory = player.inventory
        val user = magenta.user.getUser(player.uniqueId)
        val delay = magenta.config.getLong("repair-cooldown")

        if (inventory.itemInMainHand.type.isEmpty || inventory.itemInMainHand.type.isAir || inventory.itemInMainHand.isEmpty)
            return player.sendMessage(magenta.locale.translation("magenta.command.repair.error.empty.hand"))

        val timeLeft = user.getRemainingCooldown("repair")
        if (user.hasDelay("repair") && !player.hasPermission(Permissions.REPAIR_DELAY_EXEMPT))
            return magenta.commandHelper.delayMessage(player, "magenta.command.repair.error.delay", timeLeft)

        if (delay != 0L && delay != -1L || !player.hasPermission(Permissions.REPAIR_DELAY_EXEMPT)) {
            user.setDelay(Duration.ofSeconds(delay), "repair")
        }

        magenta.commandHelper.repairItemFromHand(player)
    }

    @ProxiedBy("fixall")
    @Command("repair all")
    @Permission("magenta.repair.all")
    @CommandDescription("This command repair all your items in inventory")
    fun onRepairAll(player: Player) {
        val user = magenta.user.getUser(player.uniqueId)
        val delay = magenta.config.getLong("repair-cooldown")

        val inventory = player.inventory
        if (inventory.isEmpty)
            return player.sendMessage(magenta.locale.translation("magenta.command.repair.error.empty.inventory"))

        val timeLeft = user.getRemainingCooldown("repair")
        if (user.hasDelay("repair") && !player.hasPermission(Permissions.REPAIR_DELAY_EXEMPT))
            return magenta.commandHelper.delayMessage(player, "magenta.command.repair.error.delay", timeLeft)

        if (delay != 0L && delay != -1L || !player.hasPermission(Permissions.REPAIR_DELAY_EXEMPT)) {
            user.setDelay(Duration.ofSeconds(delay), "repair")
        }

        magenta.commandHelper.repairItems(player)

        player.sendMessage(magenta.locale.translation("magenta.command.repair.success.all"))
    }

    @ProxiedBy("fixall")
    @Command("repair all <target>")
    @Permission("magenta.repair.all.other")
    @CommandDescription("This command repair others player all items in inventory")
    fun onRepairAllProxy(commandSender: CommandSender,  @Argument(value = "target", suggestions = "players") target: Player) {
        val inventory = target.inventory
        if (inventory.isEmpty)
            return commandSender.sendMessage(magenta.locale.translation("magenta.command.repair.error.empty.inventory"))

        magenta.commandHelper.repairItems(target)

        target.sendMessage(magenta.locale.translation("magenta.command.repair.success.all"))
        commandSender.sendMessage(
            magenta.locale.translation("magenta.command.repair.success.all.to", TagResolver.resolver(
                    Placeholder.parsed("player", target.name)
                )
            )
        )
    }



}