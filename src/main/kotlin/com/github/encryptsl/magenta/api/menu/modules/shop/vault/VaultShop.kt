package com.github.encryptsl.magenta.api.menu.modules.shop.vault

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.config.UniversalConfig
import com.github.encryptsl.magenta.api.menu.MenuUI
import com.github.encryptsl.magenta.api.menu.modules.shop.economy.models.ShopPaymentHolder
import com.github.encryptsl.magenta.api.menu.modules.shop.helpers.ShopHelper
import com.github.encryptsl.magenta.api.menu.provider.templates.MenuExtender
import com.github.encryptsl.magenta.common.utils.ModernText
import dev.triumphteam.gui.builder.item.ItemBuilder
import dev.triumphteam.gui.components.GuiType
import dev.triumphteam.gui.guis.Gui
import dev.triumphteam.gui.guis.PaginatedGui
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Material
import org.bukkit.entity.HumanEntity

class VaultShop(private val magenta: Magenta) : MenuExtender {

    private val vaultShopPaymentMethods: VaultShopPaymentMethods by lazy { VaultShopPaymentMethods(magenta) }
    private val menuUI: MenuUI by lazy { MenuUI(magenta) }
    private val simpleMenu = menuUI.SimpleMenu(magenta)
    private val paginationMenu = menuUI.PaginationMenu(magenta, this)

    override fun openMenu(player: HumanEntity) {
        val gui: Gui = simpleMenu.simpleGui(magenta.shopConfig.getConfig().getString("menu.gui.display").toString(),
            magenta.shopConfig.getConfig().getInt("menu.gui.size", 6), GuiType.CHEST)

        menuUI.useAllFillers(gui.filler, magenta.shopConfig.getConfig())

        val menuCategories = magenta.shopConfig.getConfig().getConfigurationSection("menu.categories")?.getKeys(false) ?: return

        for (category in menuCategories) {
            val material = Material.getMaterial(
                magenta.shopConfig.getConfig().getString("menu.categories.$category.icon").toString()
            ) ?: continue

            if (!magenta.shopConfig.getConfig().contains("menu.categories.$category.name"))
                return player.sendMessage(magenta.localeConfig.translation("magenta.menu.error.not.defined.name",
                    Placeholder.parsed("category", category)
                ))

            if (!magenta.shopConfig.getConfig().contains("menu.categories.$category.slot"))
                return player.sendMessage(magenta.localeConfig.translation("magenta.menu.error.not.defined.slot",
                    Placeholder.parsed("category", category)
                ))

            if (!magenta.shopConfig.getConfig().contains("menu.categories.$category.icon"))
                return player.sendMessage(magenta.localeConfig.translation("magenta.menu.error.not.defined.icon",
                    Placeholder.parsed("category", category)
                ))

            val name = magenta.shopConfig.getConfig().getString("menu.categories.$category.name").toString()
            val item = ItemBuilder.from(
                magenta.itemFactory.shopItem(material, name)
            ).asGuiItem { action ->
                if (action.isRightClick || action.isLeftClick) {
                    simpleMenu.clickSound(action.whoClicked, magenta.shopConfig.getConfig())
                    return@asGuiItem openCategory(action.whoClicked, category)
                }
                action.isCancelled = true
            }

            gui.setItem(magenta.shopConfig.getConfig().getInt("menu.categories.$category.slot"), item)
        }
        gui.open(player)
    }

    fun openCategory(player: HumanEntity, type: String) {
        val shopCategory = UniversalConfig(magenta, "menu/shop/categories/$type.yml")
        if (!shopCategory.fileExist())
            return player.sendMessage(
                magenta.localeConfig.translation("magenta.command.shop.error.category.not.exist",
                    Placeholder.parsed("category", type))
            )

        if (!player.hasPermission("magenta.shop.category.$type") || !player.hasPermission("magenta.shop.category.*"))
            return player.sendMessage(
                magenta.localeConfig.translation("magenta.command.shop.error.category.permission",
                    Placeholder.parsed("category", type)
            ))

        if (!shopCategory.getConfig().contains("menu.items"))
            return player.sendMessage(magenta.localeConfig.translation("magenta.menu.error.not.defined.items",
                Placeholder.parsed("category", type)
            ))

        val name = magenta.shopConfig.getConfig().getString("menu.gui.categoryName").toString()
        val categoryName = magenta.shopConfig.getConfig().getString("menu.categories.$type.name").toString()

        val gui: PaginatedGui = paginationMenu.paginatedGui(ModernText.miniModernText(name,
            Placeholder.parsed("category", categoryName)
        ), shopCategory.getConfig().getInt("menu.gui.size", 6))

        menuUI.useAllFillers(gui.filler, shopCategory.getConfig())

        val menuItems = shopCategory.getConfig().getConfigurationSection("menu.items")?.getKeys(false) ?: return

        for (item in menuItems) {
            if (!shopCategory.getConfig().contains("menu.items.$item")) continue
            val material = Material.getMaterial(
                shopCategory.getConfig().getString("menu.items.${item}.icon").toString()
            ) ?: continue
            val itemName = shopCategory.getConfig().getString("menu.items.${item}.name") ?: material.name
            val buyPrice = shopCategory.getConfig().getDouble("menu.items.${item}.buy.price")
            val sellPrice = shopCategory.getConfig().getDouble("menu.items.${item}.sell.price")

            val isBuyAllowed = shopCategory.getConfig().contains("menu.items.${item}.buy.price")
            val isSellAllowed = shopCategory.getConfig().contains("menu.items.${item}.sell.price")
            val commands = shopCategory.getConfig().getStringList("menu.items.${item}.buy.commands")

            val guiItem = ItemBuilder.from(
                magenta.itemFactory.shopItem(
                    itemName,
                    material,
                    buyPrice,
                    sellPrice,
                    magenta.shopConfig.getConfig()
                )
            ).asGuiItem()

            guiItem.setAction { action ->
                // BUY BY STACK = 64
                if (action.isShiftClick && action.isLeftClick) {
                    paginationMenu.clickSound(action.whoClicked, shopCategory.getConfig())
                    return@setAction vaultShopPaymentMethods.buy(
                        ShopPaymentHolder(
                            magenta.itemFactory.shopItem(material, 64, itemName),
                            ShopHelper.calcPrice(64, buyPrice),
                            isBuyAllowed
                        ), null, action)
                }

                // BUY BY ONE ITEM = 1
                if (action.isLeftClick) {
                    paginationMenu.clickSound(action.whoClicked, shopCategory.getConfig())
                    return@setAction vaultShopPaymentMethods.buy(
                        ShopPaymentHolder(
                            magenta.itemFactory.shopItem(material, itemName),
                            ShopHelper.calcPrice(1, buyPrice), isBuyAllowed
                        ), commands, action)
                }

                // SELL BY STACK = 64
                if (action.isShiftClick && action.isRightClick) {
                    paginationMenu.clickSound(action.whoClicked, shopCategory.getConfig())
                    for (i in 0..35) {
                        if (player.inventory.getItem(i)?.type == material) {
                            val itemStack = player.inventory.getItem(i)
                            return@setAction vaultShopPaymentMethods.sell(
                                ShopPaymentHolder(
                                    itemStack!!,
                                    ShopHelper.calcPrice(itemStack.amount, sellPrice), isSellAllowed
                                ), action)
                        }
                    }
                    return@setAction
                }

                // SELL BY ONE ITEM = 1
                if (action.isRightClick) {
                    paginationMenu.clickSound(action.whoClicked, shopCategory.getConfig())
                    return@setAction vaultShopPaymentMethods.sell(
                        ShopPaymentHolder(magenta.itemFactory.shopItem(material, 1, itemName),
                            ShopHelper.calcPrice(1, sellPrice), isSellAllowed)
                        , action)
                }
                action.isCancelled = true
            }
            gui.addItem(guiItem)
        }
        paginationMenu.paginatedControlButtons(player, shopCategory.getConfig(), gui)
        gui.open(player)
    }
}