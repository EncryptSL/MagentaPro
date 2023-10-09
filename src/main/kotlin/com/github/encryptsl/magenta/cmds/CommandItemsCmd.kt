package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import cloud.commandframework.annotations.specifier.Greedy
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.CommandItemManager
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class CommandItemsCmd(magenta: Magenta) {

    private val itemCommandItemManager = CommandItemManager(magenta)

    @CommandMethod("commanditem|ci create <item>")
    @CommandPermission("magenta.citem.create")
    fun onCommandItemCreate(
        commandSender: CommandSender,
        @Argument(value = "item") itemName: String
    ) {
        itemCommandItemManager.createItem(commandSender, itemName)
    }

    @CommandMethod("commanditem|ci setName <item> <name>")
    @CommandPermission("magenta.citem.set.name")
    fun onCommandItemSetName(
        commandSender: CommandSender,
        @Argument(value = "item") itemName: String,
        @Argument(value = "name") @Greedy name: String
    ) {
        itemCommandItemManager.setName(commandSender, itemName, name)
    }

    @CommandMethod("commanditem|ci setMaterial <item> <material>")
    @CommandPermission("magenta.citem.set.material")
    fun onCommandItemSetName(
        commandSender: CommandSender,
        @Argument(value = "item", suggestions = "citems") itemName: String,
        @Argument(value = "material", suggestions = "materials") material: Material
    ) {
        itemCommandItemManager.setMaterial(commandSender, itemName, material)
    }

    @CommandMethod("commanditem|ci setlore <item> <lore>")
    @CommandPermission("magenta.citem.set.lore")
    fun onCommandItemSetLore(
        commandSender: CommandSender,
        @Argument(value = "item") itemName: String,
        @Argument(value = "lore") @Greedy lore: String
    ) {
        val loreSplit = lore.split(",")
        itemCommandItemManager.setLore(commandSender, itemName, loreSplit)
    }

    @CommandMethod("commanditem|ci setcommand <item> <command>")
    @CommandPermission("magenta.citem.set.command")
    fun onCommandItemSetCommand(
        commandSender: CommandSender,
        @Argument(value = "item") itemName: String,
        @Argument(value = "command") @Greedy command: String
    ) {
        itemCommandItemManager.setCommand(commandSender, itemName, command)
    }

    @CommandMethod("commanditem|ci give <item> <amount> <player>")
    @CommandPermission("magenta.citem.give")
    fun onCommandItemGive(
        commandSender: CommandSender,
        @Argument(value = "item") itemName: String,
        @Argument(value = "amount") amount: Int,
        @Argument(value = "player", suggestions = "players") target: Player
    ) {
        itemCommandItemManager.giveCommandItem(commandSender, itemName, amount, target)
    }

}