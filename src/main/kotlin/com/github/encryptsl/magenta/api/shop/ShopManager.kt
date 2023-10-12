package com.github.encryptsl.magenta.api.shop

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.ItemFactory
import com.github.encryptsl.magenta.api.ShopConfig
import com.github.encryptsl.magenta.common.hook.vault.VaultHook
import com.github.encryptsl.magenta.common.utils.ModernText
import dev.triumphteam.gui.builder.item.ItemBuilder
import dev.triumphteam.gui.components.GuiType
import dev.triumphteam.gui.guis.Gui
import dev.triumphteam.gui.guis.GuiItem
import dev.triumphteam.gui.guis.PaginatedGui
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Material
import org.bukkit.entity.Player

class ShopManager(private val magenta: Magenta) {

    private val itemFactory : ItemFactory by lazy { ItemFactory() }
    private val vault: VaultHook by lazy { VaultHook(magenta) }
    private val shopInventory: ShopInventory by lazy { ShopInventory(magenta, vault) }

    fun openShop(player: Player) {
        val gui: Gui = Gui.gui()
            .title(ModernText.miniModernText(magenta.shopConfig.getConfig().getString("shop.gui.name").toString()))
            .type(GuiType.CHEST)
            .rows(6)
            .disableItemPlace()
            .disableItemTake()
            .disableItemDrop()
            .disableItemSwap()
            .create()

        if (magenta.shopConfig.getConfig().contains("shop.gui.fill")) {
            if (magenta.shopConfig.getConfig().contains("shop.gui.fill.border")) {
                gui.filler.fillBorder(GuiItem(Material.valueOf(magenta.shopConfig.getConfig().getString("shop.gui.fill.border.icon").toString())))
            }
            if (magenta.shopConfig.getConfig().contains("shop.gui.fill.top")) {
                gui.filler.fillTop(GuiItem(Material.valueOf(magenta.shopConfig.getConfig().getString("shop.gui.fill.top.icon").toString())))
            }
            if (magenta.shopConfig.getConfig().contains("shop.gui.fill.bottom")) {
                gui.filler.fillBottom(GuiItem(Material.valueOf(magenta.shopConfig.getConfig().getString("shop.gui.fill.bottom.icon").toString())))
            }
            if (magenta.shopConfig.getConfig().contains("shop.gui.fill.all")) {
                gui.filler.fill(GuiItem(Material.valueOf(magenta.shopConfig.getConfig().getString("shop.gui.fill.all.icon").toString())))
            }
        }

        for (material in Material.entries) {
            for (category in magenta.shopConfig.getConfig().getConfigurationSection("shop.categories")?.getKeys(false)!!) {

                if (!magenta.shopConfig.getConfig().contains("shop.categories.$category.name"))
                    return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.shop.error.not.defined.name")))

                if (!magenta.shopConfig.getConfig().contains("shop.categories.$category.slot"))
                    return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.shop.error.not.defined.slot")))

                if (!magenta.shopConfig.getConfig().contains("shop.categories.$category.open"))
                    return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.shop.error.not.defined.open")))

                if (!magenta.shopConfig.getConfig().contains("shop.categories.$category.icon"))
                    return player.sendMessage("Icon isn't set !")

                if (magenta.shopConfig.getConfig().getString("shop.categories.$category.icon").equals(material.name, ignoreCase = true)) {
                    val item = ItemBuilder.from(itemFactory.shopItem(
                        material,
                        magenta.shopConfig.getConfig().getString("shop.categories.$category.name").toString()
                    )).asGuiItem { action ->
                        if (action.isRightClick || action.isLeftClick) {
                            openCategory(player, category)
                        }
                        action.isCancelled = true
                    }

                    gui.setItem(magenta.shopConfig.getConfig().getInt("shop.categories.$category.slot"), item)
                    gui.open(player)
                }
            }
        }
    }

    fun openCategory(player: Player, type: String) {
        val shopCategory = ShopConfig(magenta, "shop/categories/$type.yml")
        if (!player.hasPermission("magenta.shop.category.$type") || !player.hasPermission("magenta.shop.category.*"))
            return player.sendMessage(
                ModernText.miniModernText(
                    magenta.localeConfig.getMessage("magenta.command.shop.error.category.permission"),
                    Placeholder.parsed("category", type)
                )
            )

        val gui: PaginatedGui = Gui.paginated()
            .title(
                ModernText.miniModernText(
                    magenta.shopConfig.getConfig().getString("shop.gui.categoryName").toString(), TagResolver.resolver(
                        Placeholder.parsed("category", type.uppercase()),
                    )
                )
            ).rows(6)
            .disableItemPlace()
            .disableItemDrop()
            .disableItemSwap()
            .disableItemTake()
            .create()

        if (shopCategory.getConfig().contains("shop.gui.fill")) {
            if (shopCategory.getConfig().contains("shop.gui.fill.border")) {
                gui.filler.fillBorder(GuiItem(Material.valueOf(shopCategory.getConfig().getString("shop.gui.fill.border.icon").toString())))
            }
            if (shopCategory.getConfig().contains("shop.gui.fill.top")) {
                gui.filler.fillTop(GuiItem(Material.valueOf(shopCategory.getConfig().getString("shop.gui.fill.top.icon").toString())))
            }
            if (shopCategory.getConfig().contains("shop.gui.fill.bottom")) {
                gui.filler.fillBottom(GuiItem(Material.valueOf(shopCategory.getConfig().getString("shop.gui.fill.bottom.icon").toString())))
            }
            if (shopCategory.getConfig().contains("shop.gui.fill.all")) {
                gui.filler.fill(GuiItem(Material.valueOf(shopCategory.getConfig().getString("shop.gui.fill.all.icon").toString())))
            }
        }

        for (material in Material.entries) {
            if (shopCategory.getConfig().contains("shop.items.${material.name}")) {
                val buyPrice = shopCategory.getConfig().getInt("shop.items.${material.name}.buy.price")
                val sellPrice = shopCategory.getConfig().getInt("shop.items.${material.name}.sell.price")

                val isBuyAllowed = shopCategory.getConfig().contains("shop.items.${material.name}.buy.price")
                val isSellAllowed = shopCategory.getConfig().contains("shop.items.${material.name}.sell.price")

                val guiItem = ItemBuilder.from(
                    itemFactory.shopItem(
                        material,
                        buyPrice,
                        sellPrice,
                        magenta.shopConfig.getConfig().getStringList("shop.gui.item_lore")
                    )
                ).asGuiItem()

                guiItem.setAction { action ->
                    if (action.isShiftClick && action.isLeftClick) {
                        shopInventory.buyItem(itemFactory.shopItem(material, 64), isBuyAllowed, buyPrice, action)
                        return@setAction
                    }

                    if (action.isLeftClick) {
                        ShopInventory(magenta, vault).buyItem(itemFactory.shopItem(material, 1), isBuyAllowed, buyPrice, action)
                        return@setAction
                    }


                    if (action.isShiftClick && action.isRightClick) {
                        for (i in 0..35) {
                            if (player.inventory.getItem(i)?.type == material) {
                                shopInventory.sellItem(player.inventory.getItem(i)!!, isSellAllowed, sellPrice, action)
                                return@setAction
                            }
                        }
                        return@setAction
                    }

                    if (action.isRightClick) {
                        shopInventory.sellItem(itemFactory.shopItem(material, 1), isSellAllowed, sellPrice, action)
                        return@setAction
                    }
                    action.isCancelled = true
                }

                gui.addItem(guiItem)
            }
            ShopButtons.paginationButton(player, material, gui, shopCategory, "previous", magenta)
            ShopButtons.closeButton(player, material, gui, shopCategory, shopCategory.getConfig().getString("shop.gui.button.close.action").toString(), magenta, this)
            ShopButtons.paginationButton(player, material, gui, shopCategory, "next", magenta)
        }
        gui.open(player)
    }
}