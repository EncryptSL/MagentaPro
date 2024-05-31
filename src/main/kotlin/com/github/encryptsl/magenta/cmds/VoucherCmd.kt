package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.kmono.lib.api.commands.AnnotationFeatures
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.model.VoucherManager
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotation.specifier.Greedy
import org.incendo.cloud.annotation.specifier.Range
import org.incendo.cloud.annotations.*
import org.incendo.cloud.paper.LegacyPaperCommandManager
import org.incendo.cloud.suggestion.Suggestion
import java.util.concurrent.CompletableFuture

@Suppress("UNUSED")
class VoucherCmd(private val magenta: Magenta) : AnnotationFeatures {

    private val voucher = VoucherManager(magenta)

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: LegacyPaperCommandManager<CommandSender>
    ) {
        commandManager.parserRegistry().registerSuggestionProvider("vouchers") {_, _ ->
            return@registerSuggestionProvider CompletableFuture.completedFuture(magenta.vouchers.getConfig().getConfigurationSection("vouchers")
                ?.getKeys(false)
                ?.mapNotNull { a -> Suggestion.suggestion(a.toString()) }!!)
        }
        annotationParser.parse(this)
    }

    @Command("voucher create <item>")
    @Permission("magenta.voucher.create")
    @CommandDescription("This command create voucher item.")
    fun onCommandItemCreate(
        commandSender: CommandSender,
        @Argument(value = "item") itemName: String
    ) {
        voucher.createItem(commandSender, itemName)
    }

    @Command("voucher setName <item> <name>")
    @Permission("magenta.citem.set.name")
    @CommandDescription("This command set to voucher item name.")
    fun onCommandItemSetName(
        commandSender: CommandSender,
        @Argument(value = "item", suggestions = "vouchers") itemName: String,
        @Argument(value = "name") @Greedy name: String
    ) {
        voucher.setName(commandSender, itemName, name)
    }

    @Command("voucher setMaterial <item> <material>")
    @Permission("magenta.citem.set.material")
    @CommandDescription("This command set to voucher item material.")
    fun onCommandItemSetName(
        commandSender: CommandSender,
        @Argument(value = "item", suggestions = "vouchers") itemName: String,
        @Argument(value = "material", suggestions = "materials") material: Material
    ) {
        voucher.setMaterial(commandSender, itemName, material)
    }

    @Command("voucher setlore <item> <lore>")
    @Permission("magenta.voucher.set.lore")
    @CommandDescription("This command set to voucher item lore.")
    fun onCommandItemSetLore(
        commandSender: CommandSender,
        @Argument(value = "item", suggestions = "vouchers") itemName: String,
        @Argument(value = "lore") @Greedy lore: String
    ) {
        val loreSplit = lore.split(",")
        voucher.setLore(commandSender, itemName, loreSplit)
    }

    @Command("voucher setcommand <item> <command>")
    @Permission("magenta.voucher.set.command")
    @CommandDescription("This command set to voucher item commands.")
    fun onCommandItemSetCommand(
        commandSender: CommandSender,
        @Argument(value = "item", suggestions = "citems") itemName: String,
        @Argument(value = "command") @Greedy command: String
    ) {
        voucher.setCommand(commandSender, itemName, command)
    }

    @Command("voucher give <item> <amount> <player>")
    @Permission("magenta.voucher.give")
    @CommandDescription("This command give activation item.")
    fun onCommandItemGive(
        commandSender: CommandSender,
        @Argument(value = "item", suggestions = "vouchers") itemName: String,
        @Argument(value = "amount") @Range(min = "1", max = "10") amount: Int,
        @Argument(value = "player", suggestions = "players") target: Player
    ) {
        voucher.giveCommandItem(commandSender, itemName, amount, target)
    }

}