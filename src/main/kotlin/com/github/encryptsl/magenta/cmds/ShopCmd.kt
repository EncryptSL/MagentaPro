package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.commands.AnnotationFeatures
import com.github.encryptsl.magenta.api.menu.modules.shop.credits.CreditShop
import com.github.encryptsl.magenta.api.menu.modules.shop.vault.VaultShop
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.*
import org.incendo.cloud.paper.PaperCommandManager
import org.incendo.cloud.suggestion.Suggestion
import java.util.concurrent.CompletableFuture

@Suppress("UNUSED")
class ShopCmd(private val magenta: Magenta) : AnnotationFeatures {

    private val vaultShop = VaultShop(magenta)
    private val creditShop = CreditShop(magenta)

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: PaperCommandManager<CommandSender>
    ) {
        commandManager.parserRegistry().registerSuggestionProvider("shops") {_, _ ->
            return@registerSuggestionProvider CompletableFuture.completedFuture(
                magenta.shopConfig.getConfig().getConfigurationSection("menu.categories")
                    ?.getKeys(false)
                    ?.mapNotNull { Suggestion.suggestion(it.toString()) }!!
            )
        }
        commandManager.parserRegistry().registerSuggestionProvider("creditshops") {_, _ ->
            return@registerSuggestionProvider CompletableFuture.completedFuture(
                magenta.creditShopConfig.getConfig().getConfigurationSection("menu.categories")
                    ?.getKeys(false)
                    ?.mapNotNull { Suggestion.suggestion(it.toString()) }!!
            )
        }
        annotationParser.parse(this)
    }

    @Command("shop")
    @Permission("magenta.shop")
    @CommandDescription("This command open shop gui")
    fun onShop(player: Player) {
        vaultShop.openMenu(player)
    }

    @Command("shop open <type>")
    @Permission("magenta.shop.open")
    @CommandDescription("This command open shop category gui")
    fun onShopOpen(player: Player, @Argument(value = "type", suggestions = "shops") type: String) {
        vaultShop.openCategory(player, type)
    }

    @Command("creditshop|cshop")
    @Permission("magenta.credit.shop")
    @CommandDescription("This command open credit shop")
    fun onCreditShop(player: Player) {
        creditShop.openMenu(player)
    }

    @Command("creditshop|cshop open <type>")
    @Permission("magenta.credit.shop.open")
    @CommandDescription("This command open credit shop category")
    fun onOpenCreditShop(player: Player, @Argument(value = "type", suggestions = "creditshops") type: String) {
        creditShop.openCategory(player, type)
    }
}