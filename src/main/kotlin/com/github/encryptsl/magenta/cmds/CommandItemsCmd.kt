package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.commands.AnnotationFeatures
import com.github.encryptsl.magenta.common.model.CommandItemManager
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotation.specifier.Greedy
import org.incendo.cloud.annotation.specifier.Range
import org.incendo.cloud.annotations.*
import org.incendo.cloud.paper.PaperCommandManager
import org.incendo.cloud.suggestion.Suggestion
import java.util.concurrent.CompletableFuture

@Suppress("UNUSED")
class CommandItemsCmd(private val magenta: Magenta) : AnnotationFeatures {

    private val itemCommandItemManager = CommandItemManager(magenta)

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: PaperCommandManager<CommandSender>
    ) {
        commandManager.parserRegistry().registerSuggestionProvider("citems") {_, _ ->
            return@registerSuggestionProvider CompletableFuture.completedFuture(magenta.cItems.getConfig().getConfigurationSection("citems")
                ?.getKeys(false)
                ?.mapNotNull { a -> Suggestion.simple(a.toString()) }!!)
        }
        annotationParser.parse(this)
    }

    @Command("commanditem|ci create <item>")
    @Permission("magenta.citem.create")
    @CommandDescription("This command create activation item.")
    fun onCommandItemCreate(
        commandSender: CommandSender,
        @Argument(value = "item") itemName: String
    ) {
        itemCommandItemManager.createItem(commandSender, itemName)
    }

    @Command("commanditem|ci setName <item> <name>")
    @Permission("magenta.citem.set.name")
    @CommandDescription("This command set to activation item name.")
    fun onCommandItemSetName(
        commandSender: CommandSender,
        @Argument(value = "item", suggestions = "citems") itemName: String,
        @Argument(value = "name") @Greedy name: String
    ) {
        itemCommandItemManager.setName(commandSender, itemName, name)
    }

    @Command("commanditem|ci setMaterial <item> <material>")
    @Permission("magenta.citem.set.material")
    @CommandDescription("This command set to activation item material.")
    fun onCommandItemSetName(
        commandSender: CommandSender,
        @Argument(value = "item", suggestions = "citems") itemName: String,
        @Argument(value = "material", suggestions = "materials") material: Material
    ) {
        itemCommandItemManager.setMaterial(commandSender, itemName, material)
    }

    @Command("commanditem|ci setlore <item> <lore>")
    @Permission("magenta.citem.set.lore")
    @CommandDescription("This command set to activation item lore.")
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
    @CommandDescription("This command set to activation item commands.")
    fun onCommandItemSetCommand(
        commandSender: CommandSender,
        @Argument(value = "item", suggestions = "citems") itemName: String,
        @Argument(value = "command") @Greedy command: String
    ) {
        itemCommandItemManager.setCommand(commandSender, itemName, command)
    }

    @Command("commanditem|ci give <item> <amount> <player>")
    @Permission("magenta.citem.give")
    @CommandDescription("This command give activation item.")
    fun onCommandItemGive(
        commandSender: CommandSender,
        @Argument(value = "item", suggestions = "citems") itemName: String,
        @Argument(value = "amount") @Range(min = "1", max = "100") amount: Int,
        @Argument(value = "player", suggestions = "players") target: Player
    ) {
        itemCommandItemManager.giveCommandItem(commandSender, itemName, amount, target)
    }

}