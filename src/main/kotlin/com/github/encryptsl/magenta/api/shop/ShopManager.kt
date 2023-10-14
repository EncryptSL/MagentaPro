package com.github.encryptsl.magenta.api.shop

import com.github.encryptsl.magenta.Magenta
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
                gui.filler.fillBorder(
                    GuiItem(
                        Material.valueOf(
                            magenta.shopConfig.getConfig().getString("shop.gui.fill.border").toString()
                        )
                    )
                )
            }
            if (magenta.shopConfig.getConfig().contains("shop.gui.fill.top")) {
                gui.filler.fillTop(
                    GuiItem(
                        Material.valueOf(
                            magenta.shopConfig.getConfig().getString("shop.gui.fill.top").toString()
                        )
                    )
                )
            }
            if (magenta.shopConfig.getConfig().contains("shop.gui.fill.bottom")) {
                gui.filler.fillBottom(
                    GuiItem(
                        Material.valueOf(
                            magenta.shopConfig.getConfig().getString("shop.gui.fill.bottom").toString()
                        )
                    )
                )
            }
            if (magenta.shopConfig.getConfig().contains("shop.gui.fill.all")) {
                gui.filler.fill(
                    GuiItem(
                        Material.valueOf(
                            magenta.shopConfig.getConfig().getString("shop.gui.fill.all").toString()
                        )
                    )
                )
            }
        }

        for (material in Material.entries) {
            for (category in magenta.shopConfig.getConfig().getConfigurationSection("shop.categories")
                ?.getKeys(false)!!) {

                if (!magenta.shopConfig.getConfig().contains("shop.categories.$category.name"))
                    return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.shop.error.not.defined.name"),
                        Placeholder.parsed("category", category)
                    ))

                if (!magenta.shopConfig.getConfig().contains("shop.categories.$category.slot"))
                    return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.shop.error.not.defined.slot"),
                        Placeholder.parsed("category", category)
                    ))

                if (!magenta.shopConfig.getConfig().contains("shop.categories.$category.icon"))
                    return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.shop.error.not.defined.icon"),
                        Placeholder.parsed("category", category)
                    ))

                if (magenta.shopConfig.getConfig().getString("shop.categories.$category.icon")
                        .equals(material.name, ignoreCase = true)
                ) {
                    val item = ItemBuilder.from(
                        magenta.itemFactory.shopItem(
                            material,
                            magenta.shopConfig.getConfig().getString("shop.categories.$category.name").toString()
                        )
                    ).asGuiItem { action ->
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
        if (!shopCategory.fileExist())
            return player.sendMessage(
                ModernText.miniModernText(
                    magenta.localeConfig.getMessage("magenta.command.shop.error.category.not.exist"),
                    Placeholder.parsed("category", type)
                )
            )

        if (!player.hasPermission("magenta.shop.category.$type") || !player.hasPermission("magenta.shop.category.*"))
            return player.sendMessage(
                ModernText.miniModernText(
                    magenta.localeConfig.getMessage("magenta.command.shop.error.category.permission"),
                    Placeholder.parsed("category", type)
                )
            )

        if (!shopCategory.getConfig().contains("shop.items"))
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.shop.error.not.defined.items"),
                Placeholder.parsed("category", type)
            ))

        val gui: PaginatedGui = Gui.paginated()
            .title(ModernText.miniModernText(
                    magenta.shopConfig.getConfig().getString("shop.gui.categoryName").toString(), TagResolver.resolver(
                        Placeholder.parsed("category", magenta.shopConfig.getConfig().getString("shop.categories.$type.name").toString()),
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
                gui.filler.fillBorder(
                    GuiItem(
                        Material.valueOf(
                            shopCategory.getConfig().getString("shop.gui.fill.border").toString()
                        )
                    )
                )
            }
            if (shopCategory.getConfig().contains("shop.gui.fill.top")) {
                gui.filler.fillTop(
                    GuiItem(
                        Material.valueOf(
                            shopCategory.getConfig().getString("shop.gui.fill.top").toString()
                        )
                    )
                )
            }
            if (shopCategory.getConfig().contains("shop.gui.fill.bottom")) {
                gui.filler.fillBottom(
                    GuiItem(
                        Material.valueOf(
                            shopCategory.getConfig().getString("shop.gui.fill.bottom").toString()
                        )
                    )
                )
            }
            if (shopCategory.getConfig().contains("shop.gui.fill.all")) {
                gui.filler.fill(
                    GuiItem(
                        Material.valueOf(
                            shopCategory.getConfig().getString("shop.gui.fill.all").toString()
                        )
                    )
                )
            }
        }

        for (item in shopCategory.getConfig().getConfigurationSection("shop.items")?.getKeys(false)!!) {
            val material = Material.getMaterial(item)
            if (material != null) {
                if (shopCategory.getConfig().contains("shop.items.${material.name}")) {
                    val buyPrice = shopCategory.getConfig().getDouble("shop.items.${material.name}.buy.price")
                    val sellPrice = shopCategory.getConfig().getDouble("shop.items.${material.name}.sell.price")

                    val isBuyAllowed = shopCategory.getConfig().contains("shop.items.${material.name}.buy.price")
                    val isSellAllowed = shopCategory.getConfig().contains("shop.items.${material.name}.sell.price")

                    val guiItem = ItemBuilder.from(
                        magenta.itemFactory.shopItem(
                            material,
                            buyPrice,
                            sellPrice,
                            magenta.shopConfig.getConfig().getStringList("shop.gui.item_lore")
                        )
                    ).asGuiItem()

                    guiItem.setAction { action ->
                        if (action.isShiftClick && action.isLeftClick) {
                            shopInventory.buyItem(magenta.itemFactory.shopItem(material, 64), isBuyAllowed, buyPrice, action)
                            return@setAction
                        }

                        if (action.isLeftClick) {
                            shopInventory.buyItem(magenta.itemFactory.shopItem(material, 1), isBuyAllowed, buyPrice, action)
                            return@setAction
                        }


                        if (action.isShiftClick && action.isRightClick) {
                            for (i in 0..35) {
                                if (player.inventory.getItem(i)?.type == material) {
                                    shopInventory.sellItem(
                                        player.inventory.getItem(i)!!,
                                        isSellAllowed,
                                        sellPrice,
                                        action
                                    )
                                    return@setAction
                                }
                            }
                            return@setAction
                        }

                        if (action.isRightClick) {
                            shopInventory.sellItem(magenta.itemFactory.shopItem(material, 1), isSellAllowed, sellPrice, action)
                            return@setAction
                        }
                        action.isCancelled = true
                    }

                    gui.addItem(guiItem)
                }
            }
        }

        for (material in Material.entries) {
            ShopButtons.paginationButton(player, material, gui, shopCategory, "previous", magenta)
            ShopButtons.closeButton(
                player,
                material,
                gui,
                shopCategory,
                magenta,
                this
            )
            ShopButtons.paginationButton(player, material, gui, shopCategory, "next", magenta)
        }
        gui.open(player)
    }
}