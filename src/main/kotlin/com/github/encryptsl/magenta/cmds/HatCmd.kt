package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.kmono.lib.api.commands.AnnotationFeatures
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.annotations.AnnotationParser
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.annotations.Command
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.annotations.CommandDescription
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.annotations.Permission
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.paper.LegacyPaperCommandManager
import com.github.encryptsl.magenta.Magenta
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot

@Suppress("UNUSED")
class HatCmd(private val magenta: Magenta) : AnnotationFeatures {

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: LegacyPaperCommandManager<CommandSender>
    ) {
        annotationParser.parse(this)
    }

    @Command("hat")
    @Permission("magenta.hat")
    @CommandDescription("This command set item from hand to head")
    fun onHat(player: Player) {
        val inventory = player.inventory
        val itemInMainHand = inventory.itemInMainHand
        if (itemInMainHand.type.isEmpty || itemInMainHand.type.isAir || itemInMainHand.isEmpty)
            return player.sendMessage(magenta.locale.translation("magenta.command.hat.error.empty.hand"))

        if (!inventory.getItem(EquipmentSlot.HEAD).isEmpty || !inventory.getItem(EquipmentSlot.HEAD).type.isAir || !inventory.getItem(EquipmentSlot.HEAD).type.isEmpty) {
            player.inventory.setItem(EquipmentSlot.HAND, inventory.getItem(EquipmentSlot.HEAD))
        }

        player.inventory.setItem(EquipmentSlot.HEAD, itemInMainHand)
        player.sendMessage(magenta.locale.translation("magenta.command.hat.success.set",
            Placeholder.component("item", itemInMainHand.displayName())
        ))
    }

}