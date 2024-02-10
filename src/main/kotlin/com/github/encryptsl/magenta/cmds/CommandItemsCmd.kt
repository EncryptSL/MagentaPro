package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.CommandItemManager
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotation.specifier.Greedy
import org.incendo.cloud.annotation.specifier.Range
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class CommandItemsCmd(magenta: Magenta) {

    private val itemCommandItemManager = CommandItemManager(magenta)

    @Command("commanditem|ci create <item>")
    @Permission("magenta.citem.create")
    fun onCommandItemCreate(
        commandSender: CommandSender,
        @Argument(value = "item") itemName: String
    ) {
        itemCommandItemManager.createItem(commandSender, itemName)
    }

    @Command("commanditem|ci setName <item> <name>")
    @Permission("magenta.citem.set.name")
    fun onCommandItemSetName(
        commandSender: CommandSender,
        @Argument(value = "item", suggestions = "citems") itemName: String,
        @Argument(value = "name") @Greedy name: String
    ) {
        itemCommandItemManager.setName(commandSender, itemName, name)
    }

    @Command("commanditem|ci setMaterial <item> <material>")
    @Permission("magenta.citem.set.material")
    fun onCommandItemSetName(
        commandSender: CommandSender,
        @Argument(value = "item", suggestions = "citems") itemName: String,
        @Argument(value = "material", suggestions = "materials") material: Material
    ) {
        itemCommandItemManager.setMaterial(commandSender, itemName, material)
    }

    @Command("commanditem|ci setlore <item> <lore>")
    @Permission("magenta.citem.set.lore")
    fun onCommandItemSetLore(
        commandSender: CommandSender,
        @Argument(value = "item", suggestions = "citems") itemName: String,
        @Argument(value = "lore") @Greedy lore: String
    ) {
        val loreSplit = lore.split(",")
        itemCommandItemManager.setLore(commandSender, itemName, loreSplit)
    }

    @Command("commanditem|ci setcommand <item> <command>")
    @Permission("magenta.citem.set.command")
    fun onCommandItemSetCommand(
        commandSender: CommandSender,
        @Argument(value = "item", suggestions = "citems") itemName: String,
        @Argument(value = "command") @Greedy command: String
    ) {
        itemCommandItemManager.setCommand(commandSender, itemName, command)
    }

    @Command("commanditem|ci give <item> <amount> <player>")
    @Permission("magenta.citem.give")
    fun onCommandItemGive(
        commandSender: CommandSender,
        @Argument(value = "item", suggestions = "citems") itemName: String,
        @Argument(value = "amount") @Range(min = "1", max = "100") amount: Int,
        @Argument(value = "player", suggestions = "players") target: Player
    ) {
        itemCommandItemManager.giveCommandItem(commandSender, itemName, amount, target)
    }

}