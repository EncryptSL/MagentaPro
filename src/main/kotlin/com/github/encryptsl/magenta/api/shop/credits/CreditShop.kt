package com.github.encryptsl.magenta.api.shop.credits

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.ShopConfig
import com.github.encryptsl.magenta.api.shop.helpers.ShopButtons
import com.github.encryptsl.magenta.common.hook.creditlite.CreditLiteHook
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

class CreditShop(private val magenta: Magenta) {

    private val creditLiteHook: CreditLiteHook by lazy { CreditLiteHook(magenta) }
    private val creditShopInventory: CreditShopInventory by lazy { CreditShopInventory(magenta, creditLiteHook) }

    fun openShop(player: Player) {
        val gui: Gui = Gui.gui()
            .title(ModernText.miniModernText(magenta.zeusShopConfig.getConfig().getString("shop.gui.name").toString()))
            .type(GuiType.CHEST)
            .rows(magenta.zeusShopConfig.getConfig().getInt("shop.gui.size", 6))
            .disableItemPlace()
            .disableItemTake()
            .disableItemDrop()
            .disableItemSwap()
            .create()

        if (magenta.zeusShopConfig.getConfig().contains("shop.gui.fill")) {
            if (magenta.zeusShopConfig.getConfig().contains("shop.gui.fill.border")) {
                gui.filler.fillBorder(
                    GuiItem(
                        Material.valueOf(
                            magenta.zeusShopConfig.getConfig().getString("shop.gui.fill.border").toString()
                        )
                    )
                )
            }
            if (magenta.zeusShopConfig.getConfig().contains("shop.gui.fill.top")) {
                gui.filler.fillTop(
                    GuiItem(
                        Material.valueOf(
                            magenta.zeusShopConfig.getConfig().getString("shop.gui.fill.top").toString()
                        )
                    )
                )
            }
            if (magenta.zeusShopConfig.getConfig().contains("shop.gui.fill.bottom")) {
                gui.filler.fillBottom(
                    GuiItem(
                        Material.valueOf(
                            magenta.zeusShopConfig.getConfig().getString("shop.gui.fill.bottom").toString()
                        )
                    )
                )
            }
            if (magenta.zeusShopConfig.getConfig().contains("shop.gui.fill.all")) {
                gui.filler.fill(
                    GuiItem(
                        Material.valueOf(
                            magenta.zeusShopConfig.getConfig().getString("shop.gui.fill.all").toString()
                        )
                    )
                )
            }


            for (material in Material.entries) {
                for (category in magenta.zeusShopConfig.getConfig().getConfigurationSection("shop.categories")
                    ?.getKeys(false)!!) {

                    if (!magenta.zeusShopConfig.getConfig().contains("shop.categories.$category.name"))
                        return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.shop.error.not.defined.name"),
                            Placeholder.parsed("category", category)
                        ))

                    if (!magenta.zeusShopConfig.getConfig().contains("shop.categories.$category.slot"))
                        return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.shop.error.not.defined.slot"),
                            Placeholder.parsed("category", category)
                        ))

                    if (!magenta.zeusShopConfig.getConfig().contains("shop.categories.$category.icon"))
                        return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.shop.error.not.defined.icon"),
                            Placeholder.parsed("category", category)
                        ))

                    if (magenta.zeusShopConfig.getConfig().getString("shop.categories.$category.icon")
                            .equals(material.name, ignoreCase = true)
                    ) {
                        val item = ItemBuilder.from(
                            magenta.itemFactory.shopItem(
                                material,
                                magenta.zeusShopConfig.getConfig().getString("shop.categories.$category.name").toString()
                            )
                        ).asGuiItem { action ->
                            if (action.isRightClick || action.isLeftClick) {
                                openCategory(player, category)
                            }
                            action.isCancelled = true
                        }

                        gui.setItem(magenta.zeusShopConfig.getConfig().getInt("shop.categories.$category.slot"), item)
                        gui.open(player)
                    }
                }
            }
            gui.open(player)
        }
    }


    fun openCategory(player: Player, type: String) {
        val shopCategory = ShopConfig(magenta, "shop/categories/zeus/$type.yml")
        if (!shopCategory.fileExist())
            return player.sendMessage(
                ModernText.miniModernText(
                    magenta.localeConfig.getMessage("magenta.command.shop.error.category.not.exist"),
                    Placeholder.parsed("category", type)
                )
            )

        if (!player.hasPermission("magenta.shop.zeus") || !player.hasPermission("magenta.shop.zeus.*"))
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

                    if (!shopCategory.getConfig().contains("shop.items.${material.name}.commands"))
                        return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.shop.error.not.defined.commands"),
                            Placeholder.parsed("category", type)
                        ))

                    if (!shopCategory.getConfig().contains("shop.items.${material.name}.buy.quantity"))
                        return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.shop.error.not.defined.quantity"),
                            Placeholder.parsed("category", type)
                        ))

                    if (!shopCategory.getConfig().contains("shop.items.${material.name}.position.slot"))
                        return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.shop.error.not.defined.slot"),
                            Placeholder.parsed("category", type)
                        ))

                    val slot = shopCategory.getConfig().getInt("shop.items.${material.name}.position.slot")
                    val buyPrice = shopCategory.getConfig().getDouble("shop.items.${material.name}.buy.price")
                    val quantity = shopCategory.getConfig().getInt("shop.items.${material.name}.buy.quantity")
                    val commands = shopCategory.getConfig().getStringList("shop.items.${material.name}.commands")

                    val isBuyAllowed = shopCategory.getConfig().contains("shop.items.${material.name}.buy.price")

                    val guiItem = ItemBuilder.from(
                        magenta.itemFactory.creditShopItem(
                            material,
                            quantity,
                            buyPrice,
                            creditLiteHook.getCredits(player),
                            magenta.shopConfig.getConfig().getStringList("shop.gui.item_lore")
                        )
                    ).asGuiItem()

                    guiItem.setAction { action ->
                        if (action.isLeftClick) {
                            creditShopInventory.buyItem(action, guiItem.itemStack.displayName(), buyPrice, quantity, commands, "magenta.shop.success.buy", isBuyAllowed)
                            return@setAction
                        }
                        action.isCancelled = true
                    }

                    gui.setItem(slot, guiItem)
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