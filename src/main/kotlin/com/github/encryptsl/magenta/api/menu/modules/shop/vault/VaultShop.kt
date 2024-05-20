package com.github.encryptsl.magenta.api.menu.modules.shop.vault

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.kmono.lib.api.config.UniversalConfig
import com.github.encryptsl.kmono.lib.api.economy.models.EconomyDataPayment
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.MenuUI
import com.github.encryptsl.magenta.api.menu.modules.shop.helpers.ShopHelper
import com.github.encryptsl.magenta.api.menu.provider.templates.MenuExtender
import com.github.encryptsl.magenta.common.Permissions
import dev.triumphteam.gui.builder.item.ItemBuilder
import dev.triumphteam.gui.components.GuiType
import dev.triumphteam.gui.guis.Gui
import dev.triumphteam.gui.guis.GuiItem
import dev.triumphteam.gui.guis.PaginatedGui
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.event.inventory.InventoryClickEvent

class VaultShop(private val magenta: Magenta) : MenuExtender {

    private val vaultShopPaymentMethods: VaultShopPaymentMethods by lazy { VaultShopPaymentMethods(magenta) }
    private val menuUI: MenuUI by lazy { MenuUI(magenta) }
    private val simpleMenu = menuUI.SimpleMenu(magenta)
    private val paginationMenu = menuUI.PaginationMenu(magenta, this)

    override fun openMenu(player: HumanEntity) {
        val gui: Gui = simpleMenu.simpleGui(magenta.shopConfig.getConfig().getString("menu.gui.display").toString(),
            magenta.shopConfig.getConfig().getInt("menu.gui.size", 6), GuiType.CHEST)

        menuUI.useAllFillers(gui.filler, magenta.shopConfig.getConfig())

        gui.setDefaultClickAction { el ->
            if (el.currentItem != null && el.isLeftClick || el.isRightClick) {
                paginationMenu.clickSound(el.whoClicked, magenta.shopConfig.getConfig())
            }
        }

        val menuCategories = magenta.shopConfig.getConfig().getConfigurationSection("menu.categories")?.getKeys(false) ?: return

        for (category in menuCategories) {
            val material = Material.getMaterial(
                magenta.shopConfig.getConfig().getString("menu.categories.$category.icon").toString()
            ) ?: continue

            if (!magenta.shopConfig.getConfig().contains("menu.categories.$category.name"))
                return player.sendMessage(magenta.locale.translation("magenta.menu.error.not.defined.name",
                    Placeholder.parsed("category", category)
                ))

            if (!magenta.shopConfig.getConfig().contains("menu.categories.$category.slot"))
                return player.sendMessage(magenta.locale.translation("magenta.menu.error.not.defined.slot",
                    Placeholder.parsed("category", category)
                ))

            if (!magenta.shopConfig.getConfig().contains("menu.categories.$category.icon"))
                return player.sendMessage(magenta.locale.translation("magenta.menu.error.not.defined.icon",
                    Placeholder.parsed("category", category)
                ))

            val name = magenta.shopConfig.getConfig().getString("menu.categories.$category.name").toString()
            val item = ItemBuilder.from(
                magenta.itemFactory.shopItem(material, name)
            ).asGuiItem { action ->
                if (action.isRightClick || action.isLeftClick) {
                    return@asGuiItem openCategory(action.whoClicked, category)
                }
                action.isCancelled = true
            }

            gui.setItem(magenta.shopConfig.getConfig().getInt("menu.categories.$category.slot"), item)
        }
        gui.open(player)
    }

    fun openCategory(player: HumanEntity, type: String) {
        val shopCategory = UniversalConfig("${magenta.dataFolder}/menu/shop/categories/$type.yml")
        if (!shopCategory.exists())
            return player.sendMessage(
                magenta.locale.translation("magenta.command.shop.error.category.not.exist",
                    Placeholder.parsed("category", type))
            )

        if (!player.hasPermission(Permissions.SHOP_CATEGORY.format(type)) || !player.hasPermission(Permissions.SHOP_CATEGORY_ALL))
            return player.sendMessage(
                magenta.locale.translation("magenta.command.shop.error.category.permission",
                    Placeholder.parsed("category", type)
            ))

        if (!shopCategory.getConfig().contains("menu.items"))
            return player.sendMessage(magenta.locale.translation("magenta.menu.error.not.defined.items",
                Placeholder.parsed("category", type)
            ))

        val name = magenta.shopConfig.getConfig().getString("menu.gui.categoryName").toString()
        val categoryName = magenta.shopConfig.getConfig().getString("menu.categories.$type.name").toString()

        val gui: PaginatedGui = paginationMenu.paginatedGui(ModernText.miniModernText(name,
            Placeholder.parsed("category", categoryName)
        ), shopCategory.getConfig().getInt("menu.gui.size", 6))

        menuUI.useAllFillers(gui.filler, shopCategory.getConfig())

        gui.setDefaultClickAction { el ->
            if (el.currentItem != null && el.isLeftClick || el.isRightClick || el.isShiftClick) {
                paginationMenu.clickSound(el.whoClicked, shopCategory.getConfig())
            }
        }

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

            val actionItem = getItem(itemName, material, buyPrice, sellPrice, isBuyAllowed, isSellAllowed)

            actionItem.setAction { action ->
                // BUY BY STACK = 64
                if (action.isShiftClick && action.isLeftClick) {
                    return@setAction buy(action, material, buyPrice, 64, itemName, isBuyAllowed, commands)
                }

                // BUY BY ONE ITEM = 1
                if (action.isLeftClick) {
                    return@setAction buy(action, material, buyPrice, 1, itemName, isBuyAllowed, commands)
                }

                // SELL BY STACK = 64
                if (action.isShiftClick && action.isRightClick) {
                    val itemFromInv = player.inventory.storageContents.filter { el -> el?.type == material }.first()
                    itemFromInv?.let { return@setAction sell(action, material, sellPrice, it.amount, itemName, isSellAllowed) }
                }

                // SELL BY ONE ITEM = 1
                if (action.isRightClick) {
                    return@setAction sell(action, material, sellPrice, 1, itemName, isSellAllowed)
                }
                action.isCancelled = true
            }
            gui.addItem(actionItem)
        }
        paginationMenu.paginatedControlButtons(player, shopCategory.getConfig(), gui)
        gui.open(player)
    }

    private fun buy(
        action: InventoryClickEvent,
        material: Material,
        buyPrice: Double,
        counts: Int,
        itemName: String,
        isBuyAllowed: Boolean,
        commands: MutableList<String>?
    ) {
        vaultShopPaymentMethods.buy(
            EconomyDataPayment(
                magenta.itemFactory.shopItem(material, counts, itemName),
                ShopHelper.calcPrice(counts, buyPrice),
                isBuyAllowed
            ), commands, action)
    }

    private fun sell(
        action: InventoryClickEvent,
        material: Material,
        sellPrice: Double,
        counts: Int,
        itemName: String,
        isSellAllowed: Boolean
    ) {
        vaultShopPaymentMethods.sell(
            EconomyDataPayment(magenta.itemFactory.shopItem(material, counts, itemName),
                ShopHelper.calcPrice(counts, sellPrice), isSellAllowed)
            , action)
    }

    private fun getItem(
        itemName: String,
        material: Material,
        buyPrice: Double,
        sellPrice: Double,
        isBuyAllowed: Boolean,
        isSellAllowed: Boolean
    ): GuiItem {
        return ItemBuilder.from(
            magenta.itemFactory.shopItem(
                itemName,
                material,
                buyPrice,
                sellPrice,
                isBuyAllowed,
                isSellAllowed,
                magenta.shopConfig.getConfig()
            )
        ).asGuiItem()
    }
}