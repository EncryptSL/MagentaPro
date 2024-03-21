package com.github.encryptsl.magenta.common.model

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.ItemBuilder
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import kotlin.random.Random

class CommandItemManager(private val magenta: Magenta) {
    private val sid = Random(1000).nextInt(9999)

    fun createItem(commandSender: CommandSender, itemName: String) {
        if (magenta.cItems.getConfig().contains("citems.$itemName"))
            return commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.citem.error.exist",
                Placeholder.parsed("item", itemName)
            ))

        magenta.cItems.set("citems.$itemName.sid", sid)

        commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.citem.success.created", TagResolver.resolver(
            Placeholder.parsed("item", itemName),
            Placeholder.parsed("sid", sid.toString())
        )))
    }

    fun setName(commandSender: CommandSender, itemName: String, name: String) {
        if (!magenta.cItems.getConfig().contains("citems.$itemName"))
            return commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.citem.error.exist",
                Placeholder.parsed("item", itemName)
            ))

        magenta.cItems.set("citems.$itemName.name", "$name <white>#<sid>")

        commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.citem.success.set.name", TagResolver.resolver(
            Placeholder.parsed("item", itemName),
            Placeholder.parsed("name", name),
        )))
    }

    fun setMaterial(commandSender: CommandSender, itemName: String, material: Material) {
        if (!magenta.cItems.getConfig().contains("citems.$itemName"))
            return commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.citem.error.not.exist",
                Placeholder.parsed("item", itemName)
            ))

        magenta.cItems.set("citems.$itemName.material", material.name)

        commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.citem.success.set.material", TagResolver.resolver(
            Placeholder.parsed("item", itemName),
            Placeholder.parsed("material", material.name),
        )))
    }

    fun setCommand(commandSender: CommandSender, itemName: String, command: String) {
        if (!magenta.cItems.getConfig().contains("citems.$itemName"))
            return commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.citem.error.exist",
                Placeholder.parsed("item", itemName)
            ))

        magenta.cItems.set("citems.$itemName.command", command)

        commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.citem.success.set.command", TagResolver.resolver(
            Placeholder.parsed("item", itemName),
            Placeholder.parsed("command", command),
        )))
    }

    fun setLore(commandSender: CommandSender, itemName: String, lores: List<String>) {
        if (!magenta.cItems.getConfig().contains("citems.$itemName"))
            return commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.citem.error.exist",
                Placeholder.parsed("item", itemName)
            ))

        magenta.cItems.set("citems.$itemName.lore", lores)

        commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.citem.success.set.lore", TagResolver.resolver(
            Placeholder.parsed("item", itemName),
            Placeholder.parsed("lore", lores.toString()),
        )))
    }

    fun giveCommandItem(commandSender: CommandSender, itemName: String, amount: Int, target: Player) {
        if (!magenta.cItems.getConfig().contains("citems.${itemName}"))
            return commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.citem.error.exist",
                Placeholder.parsed("item", itemName)
            ))

        if (!magenta.cItems.getConfig().contains("citems.$itemName.name"))
            return commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.citem.error.not.set.name",
                Placeholder.parsed("item", itemName)
            ))

        if (!magenta.cItems.getConfig().contains("citems.$itemName.material"))
            return commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.citem.error.not.set.material",
                Placeholder.parsed("item", itemName)
            ))

        if (!magenta.cItems.getConfig().contains("citems.$itemName.lore"))
            return commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.citem.error.not.set.lore",
                Placeholder.parsed("item", itemName)
            ))

        if (!magenta.cItems.getConfig().contains("citems.$itemName.command"))
            return commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.citem.error.not.set.command",
                Placeholder.parsed("item", itemName)
            ))


        val materialName = magenta.cItems.getConfig().getString("citems.$itemName.material").toString()
        val sid = magenta.cItems.getConfig().getInt("citems.$itemName.sid")
        val item = magenta.cItems.getConfig().getString("citems.$itemName.name").toString()
        val lore = magenta.cItems.getConfig().getStringList("citems.$itemName.lore")
        val glowing = magenta.cItems.getConfig().getBoolean("citems.$itemName.glowing")
        val material = Material.getMaterial(materialName) ?: return


        val itemStack = ItemBuilder(material, amount)
            .setName(ModernText.miniModernText(item, Placeholder.parsed("sid", sid.toString())))
            .setGlowing(glowing)
            .addLore(lore.map { ModernText.miniModernText(it) }.toMutableList()).create()
        target.inventory.addItem(itemStack)
        target.updateInventory()

        commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.citem.success.given", TagResolver.resolver(
            Placeholder.parsed("player", target.name),
            Placeholder.component("item", itemStack.displayName()),
            Placeholder.parsed("amount", amount.toString())
        )))
    }

}