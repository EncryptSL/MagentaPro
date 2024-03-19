package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

@Suppress("UNUSED")
class HatCmd(private val magenta: Magenta) {

    @Command("hat")
    @Permission("magenta.hat")
    @CommandDescription("This command set item from hand to head")
    fun onHat(player: Player) {
        val inventory = player.inventory
        val itemInMainHand = inventory.itemInMainHand
        if (itemInMainHand.type.isEmpty || itemInMainHand.type.isAir || itemInMainHand.isEmpty)
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.hat.error.empty.hand")))

        if (!inventory.getItem(EquipmentSlot.HEAD).isEmpty || !inventory.getItem(EquipmentSlot.HEAD).type.isAir || !inventory.getItem(EquipmentSlot.HEAD).type.isEmpty) {
            player.inventory.setItem(EquipmentSlot.HAND, inventory.getItem(EquipmentSlot.HEAD))
        }

        player.inventory.setItem(EquipmentSlot.HEAD, itemInMainHand)
        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.hat.success.set"),
            Placeholder.component("item", itemInMainHand.displayName())
        ))
    }

}