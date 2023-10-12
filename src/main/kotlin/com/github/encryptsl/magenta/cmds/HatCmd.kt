package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot




@CommandDescription("Provided by plugin MagentaPro")
class HatCmd(private val magenta: Magenta) {

    @CommandMethod("hat")
    @CommandPermission("magenta.hat")
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