package com.github.encryptsl.magenta.api.shop.vault

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.config.ShopConfig
import com.github.encryptsl.magenta.api.shop.ShopPaymentInformation
import com.github.encryptsl.magenta.api.shop.helpers.ShopUI
import com.github.encryptsl.magenta.common.hook.vault.VaultHook
import com.github.encryptsl.magenta.common.utils.ModernText
import dev.triumphteam.gui.builder.item.ItemBuilder
import dev.triumphteam.gui.components.GuiType
import dev.triumphteam.gui.guis.Gui
import dev.triumphteam.gui.guis.PaginatedGui
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Material
import org.bukkit.entity.Player

class VaultShop(private val magenta: Magenta) {

    private val vault: VaultHook by lazy { VaultHook(magenta) }
    private val vaultShopInventory: VaultShopInventory by lazy { VaultShopInventory(magenta, vault) }
    private val shopUI: ShopUI by lazy { ShopUI(magenta) }

    fun openShop(player: Player) {
        val gui: Gui = shopUI.simpleGui(magenta.shopConfig.getConfig().getString("shop.gui.name").toString(),
            magenta.shopConfig.getConfig().getInt("shop.gui.size", 6), GuiType.CHEST)

        if (magenta.shopConfig.getConfig().contains("shop.gui.fill")) {
            shopUI.fillBorder(gui.filler, magenta.creditShopConfig.getConfig())
            shopUI.fillTop(gui.filler, magenta.creditShopConfig.getConfig())
            shopUI.fillBottom(gui.filler, magenta.creditShopConfig.getConfig())
            shopUI.fillSide(gui.filler, magenta.creditShopConfig.getConfig())
            shopUI.fillFull(gui.filler, magenta.creditShopConfig.getConfig())
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
                    val name = magenta.shopConfig.getConfig().getString("shop.categories.$category.name").toString()
                    val item = ItemBuilder.from(
                        magenta.itemFactory.shopItem(material, name)
                    ).asGuiItem { action ->
                        if (action.isRightClick || action.isLeftClick) {
                            openCategory(player, category)
                        }
                        action.isCancelled = true
                    }

                    gui.setItem(magenta.shopConfig.getConfig().getInt("shop.categories.$category.slot"), item)
                }
            }
        }
        gui.open(player)
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

        val name = magenta.shopConfig.getConfig().getString("shop.gui.categoryName").toString()
        val categoryName = magenta.shopConfig.getConfig().getString("shop.categories.$type.name").toString()

        val gui: PaginatedGui = shopUI.paginatedGui(ModernText.miniModernText(name,
            Placeholder.parsed("category", categoryName)
        ), shopCategory.getConfig().getInt("shop.gui.size", 6))

        if (shopCategory.getConfig().contains("shop.gui.fill")) {
            shopUI.fillBorder(gui.filler, shopCategory.getConfig())
            shopUI.fillTop(gui.filler, shopCategory.getConfig())
            shopUI.fillBottom(gui.filler, shopCategory.getConfig())
            shopUI.fillSide(gui.filler, shopCategory.getConfig())
            shopUI.fillFull(gui.filler, shopCategory.getConfig())
        }

        for (item in shopCategory.getConfig().getConfigurationSection("shop.items")?.getKeys(false)!!) {
            val material = Material.getMaterial(shopCategory.getConfig().getString("shop.items.${item}.icon").toString())
            if (material != null) {
                if (shopCategory.getConfig().contains("shop.items.$item")) {
                    val itemName = shopCategory.getConfig().getString("shop.items.${item}.name") ?: material.name
                    val buyPrice = shopCategory.getConfig().getDouble("shop.items.${item}.buy.price")
                    val sellPrice = shopCategory.getConfig().getDouble("shop.items.${item}.sell.price")

                    val isBuyAllowed = shopCategory.getConfig().contains("shop.items.${item}.buy.price")
                    val isSellAllowed = shopCategory.getConfig().contains("shop.items.${item}.sell.price")
                    val isCommand = shopCategory.getConfig().contains("shop.items.${item}.buy.commands")
                    val commands = shopCategory.getConfig().getStringList("shop.items.${item}.commands")

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
                        if (action.isShiftClick && action.isLeftClick && !isCommand) {
                            return@setAction vaultShopInventory.buy(
                                ShopPaymentInformation(magenta.itemFactory.shopItem(material, 64, itemName), buyPrice, isBuyAllowed),
                                false,
                                null,
                                action
                            )
                        }

                        if (action.isLeftClick) {
                            return@setAction vaultShopInventory.buy(
                                ShopPaymentInformation(magenta.itemFactory.shopItem(material, itemName), buyPrice, isBuyAllowed),
                                isCommand,
                                commands,
                                action
                            )
                        }


                        if (action.isShiftClick && action.isRightClick) {
                            for (i in 0..35) {
                                if (player.inventory.getItem(i)?.type == material) {
                                    return@setAction vaultShopInventory.sell(
                                        ShopPaymentInformation(player.inventory.getItem(i)!!, sellPrice, isSellAllowed),
                                        action
                                    )
                                }
                            }
                            return@setAction
                        }

                        if (action.isRightClick) {
                            return@setAction vaultShopInventory.sell(ShopPaymentInformation(magenta.itemFactory.shopItem(material, 1, itemName), sellPrice, isSellAllowed), action)
                        }
                        action.isCancelled = true
                    }
                    gui.addItem(guiItem)
                }
            }
        }
        controlButtons(player, shopUI, shopCategory, gui)
        gui.open(player)
    }

    private fun controlButtons(player: Player, shopUI: ShopUI, shopCategory: ShopConfig, gui: PaginatedGui) {
        for (material in Material.entries) {
            shopUI.previousPage(player, material, shopCategory, "previous", gui)
            shopUI.closeButton(
                player,
                material,
                gui,
                shopCategory,
                this
            )
            shopUI.nextPage(player, material, shopCategory, "next", gui)
        }
    }
}