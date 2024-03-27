package com.github.encryptsl.magenta.api.menu.modules.shop.credits

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.config.UniversalConfig
import com.github.encryptsl.magenta.api.menu.MenuUI
import com.github.encryptsl.magenta.api.menu.provider.templates.MenuExtender
import com.github.encryptsl.magenta.common.utils.ModernText
import dev.triumphteam.gui.builder.item.ItemBuilder
import dev.triumphteam.gui.components.GuiType
import dev.triumphteam.gui.guis.Gui
import dev.triumphteam.gui.guis.PaginatedGui
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player

class CreditShop(private val magenta: Magenta) : MenuExtender {

    private val creditShopPaymentMethod: CreditShopPaymetMethod by lazy { CreditShopPaymetMethod(magenta) }
    private val menuUI: MenuUI by lazy { MenuUI(magenta) }
    private val simpleMenu = menuUI.SimpleMenu(magenta)
    private val paginationMenu = menuUI.PaginationMenu(magenta, this)
    private val confirmMenu: CreditShopConfirmMenu by lazy { CreditShopConfirmMenu(magenta, menuUI) }

    override fun openMenu(player: HumanEntity) {
        val gui: Gui = simpleMenu.simpleGui(
            magenta.creditShopConfig.getConfig().getString("menu.gui.display").toString(),
            magenta.creditShopConfig.getConfig().getInt("menu.gui.size", 6),
            GuiType.CHEST
        )

        gui.setDefaultClickAction { simpleMenu.clickSound(it.whoClicked, magenta.creditShopConfig.getConfig()) }

        menuUI.useAllFillers(gui.filler, magenta.creditShopConfig.getConfig())

        for (category in magenta.creditShopConfig.getConfig().getConfigurationSection("menu.categories")?.getKeys(false)!!) {
            val material = Material.getMaterial(
                magenta.creditShopConfig.getConfig().getString("menu.categories.$category.icon").toString()
            ) ?: continue

            if (!magenta.creditShopConfig.getConfig().contains("menu.categories.$category.name"))
                return player.sendMessage(
                    magenta.localeConfig.translation("magenta.menu.error.not.defined.name", Placeholder.parsed("category", category))
                )

            if (!magenta.creditShopConfig.getConfig().contains("menu.categories.$category.slot"))
                return player.sendMessage(
                    magenta.localeConfig.translation("magenta.menu.error.not.defined.slot", Placeholder.parsed("category", category))
                )
            if (!magenta.creditShopConfig.getConfig().contains("menu.categories.$category.icon"))
                return player.sendMessage(
                    magenta.localeConfig.translation("magenta.menu.error.not.defined.icon", Placeholder.parsed("category", category))
                )

            val glowing = magenta.creditShopConfig.getConfig().getBoolean("menu.categories.$category.glowing")
            val name = magenta.creditShopConfig.getConfig().getString("menu.categories.$category.name").toString()

            val item = ItemBuilder.from(
                com.github.encryptsl.magenta.api.ItemBuilder(material, 1).setName(ModernText.miniModernText(name)).setGlowing(glowing).create()
            ).asGuiItem()

            item.setAction { action ->
                if (action.isRightClick || action.isLeftClick) {
                    return@setAction openCategory(action.whoClicked, category)
                }
                action.isCancelled = true
            }
            gui.setItem(magenta.creditShopConfig.getConfig().getInt("menu.categories.$category.slot"), item)
        }
        gui.open(player)
    }


    fun openCategory(player: HumanEntity, category: String) {
        val shopCategory = UniversalConfig(magenta, "menu/creditshop/categories/$category.yml")
        if (!shopCategory.fileExist())
            return player.sendMessage(
                magenta.localeConfig.translation("magenta.command.shop.error.category.not.exist", Placeholder.parsed("category", category))
            )

        if (!player.hasPermission("magenta.credit.shop.category.$category") || !player.hasPermission("magenta.credit.shop.category.*"))
            return player.sendMessage(
                magenta.localeConfig.translation("magenta.command.shop.error.category.permission", Placeholder.parsed("category", category))
            )

        val name = magenta.creditShopConfig.getConfig().getString("menu.gui.categoryName").toString()
        val categoryName = magenta.creditShopConfig.getConfig().getString("menu.categories.$category.name").toString()

        val gui: PaginatedGui = paginationMenu.paginatedGui(
            ModernText.miniModernText(name, Placeholder.parsed("category", categoryName)),
            shopCategory.getConfig().getInt("menu.gui.size", 6)
        )

        gui.setDefaultClickAction { simpleMenu.clickSound(it.whoClicked, shopCategory.getConfig()) }


        menuUI.useAllFillers(gui.filler, shopCategory.getConfig())

        if (shopCategory.getConfig().contains("menu.custom-items")) {
            menuUI.customItems(player, categoryName, shopCategory.getConfig(), gui)
        }

        if (!shopCategory.getConfig().contains("menu.items")) return


        for (item in shopCategory.getConfig().getConfigurationSection("menu.items")?.getKeys(false)!!) {
            if (!shopCategory.getConfig().contains("menu.items.$item")) continue
            val material = Material.getMaterial(shopCategory.getConfig().getString("menu.items.$item.icon").toString()) ?: continue

            if (!shopCategory.getConfig().contains("menu.items.$item.name"))
                return player.sendMessage(
                    magenta.localeConfig.translation("magenta.menu.error.not.defined.name", Placeholder.parsed("category", category))
                )

            if (!shopCategory.getConfig().contains("menu.items.$item.position.slot"))
                return player.sendMessage(
                    magenta.localeConfig.translation("magenta.menu.error.not.defined.slot", Placeholder.parsed("category", category))
                )

            if (!shopCategory.getConfig().contains("menu.items.$item.commands"))
                return player.sendMessage(
                    magenta.localeConfig.translation("magenta.menu.error.not.defined.commands", Placeholder.parsed("category", category))
                )

            if (!shopCategory.getConfig().contains("menu.items.$item.buy.quantity"))
                return player.sendMessage(
                    magenta.localeConfig.translation("magenta.menu.error.not.defined.quantity", Placeholder.parsed("category", category))
                )

            val itemName = shopCategory.getConfig().getString("menu.items.$item.name").toString()
            val slot = shopCategory.getConfig().getInt("menu.items.$item.position.slot")
            val hasOptions = shopCategory.getConfig().contains("menu.items.$item.options")
            val glowing = shopCategory.getConfig().getBoolean("menu.items.$item.options.glowing")
            val isPotion = shopCategory.getConfig().getBoolean("menu.items.$item.options.potion")
            val hasColor = shopCategory.getConfig().contains("menu.items.$item.options.color")
            val color = shopCategory.getConfig().getInt("menu.items.$item.options.color")
            val buyPrice = shopCategory.getConfig().getDouble("menu.items.$item.buy.price")
            val quantity = shopCategory.getConfig().getInt("menu.items.$item.buy.quantity")
            val isBuyAllowed = shopCategory.getConfig().contains("menu.items.$item.buy.price")

            val guiItem = ItemBuilder.from(
                magenta.itemFactory.creditShopItem(
                    player as Player,
                    material,
                    itemName,
                    quantity,
                    buyPrice,
                    glowing,
                    hasOptions,
                    isPotion,
                    hasColor,
                    color,
                    magenta.creditShopConfig.getConfig().getStringList("menu.gui.item_lore")
                )
            ).asGuiItem()

            guiItem.setAction { action ->
                if (action.isLeftClick) {
                    if (!magenta.creditShopConfig.getConfig().getBoolean("menu.gui.confirm_required")) {
                        return@setAction creditShopPaymentMethod.buyItem(action.whoClicked, shopCategory.getConfig(), item, guiItem.itemStack.displayName(), isBuyAllowed)
                    }
                    return@setAction confirmMenu.openConfirmMenu(
                        player = action.whoClicked,
                        item = item,
                        category = category,
                        categoryConfig = shopCategory.getConfig(),
                        creditShopPaymetMethod = creditShopPaymentMethod,
                        displayName =  guiItem.itemStack.displayName(),
                        creditShop = this,
                        isBuyAllowed = isBuyAllowed
                    )
                }
                action.isCancelled = true
            }
            gui.setItem(slot, guiItem)
        }
        paginationMenu.paginatedControlButtons(player, shopCategory.getConfig(), gui)
        gui.open(player)
    }
}