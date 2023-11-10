package com.github.encryptsl.magenta.api.shop.credits

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.config.ShopConfig
import com.github.encryptsl.magenta.api.shop.helpers.ShopUI
import com.github.encryptsl.magenta.common.hook.creditlite.CreditLiteHook
import com.github.encryptsl.magenta.common.utils.ModernText
import dev.triumphteam.gui.builder.item.ItemBuilder
import dev.triumphteam.gui.components.GuiType
import dev.triumphteam.gui.guis.Gui
import dev.triumphteam.gui.guis.PaginatedGui
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Material
import org.bukkit.entity.Player

class CreditShop(private val magenta: Magenta) {

    private val creditLiteHook: CreditLiteHook by lazy { CreditLiteHook(magenta) }
    private val creditShopInventory: CreditShopInventory by lazy { CreditShopInventory(magenta, creditLiteHook) }
    private val shopUI: ShopUI by lazy {ShopUI(magenta)}

    fun openShop(player: Player) {
        val gui: Gui = shopUI.simpleGui(
            magenta.creditShopConfig.getConfig().getString("shop.gui.name").toString(),
            magenta.creditShopConfig.getConfig().getInt("shop.gui.size", 6),
            GuiType.CHEST
        )

        if (magenta.creditShopConfig.getConfig().contains("shop.gui.fill")) {
            shopUI.fillBorder(gui.filler, magenta.creditShopConfig.getConfig())
            shopUI.fillTop(gui.filler, magenta.creditShopConfig.getConfig())
            shopUI.fillBottom(gui.filler, magenta.creditShopConfig.getConfig())
            shopUI.fillAll(gui.filler, magenta.creditShopConfig.getConfig())
        }


        for (material in Material.entries) {
            for (category in magenta.creditShopConfig.getConfig().getConfigurationSection("shop.categories")
                ?.getKeys(false)!!) {

                if (!magenta.creditShopConfig.getConfig().contains("shop.categories.$category.name"))
                    return player.sendMessage(
                        ModernText.miniModernText(
                            magenta.localeConfig.getMessage("magenta.shop.error.not.defined.name"),
                            Placeholder.parsed("category", category)
                        )
                    )

                if (!magenta.creditShopConfig.getConfig().contains("shop.categories.$category.slot"))
                    return player.sendMessage(
                        ModernText.miniModernText(
                            magenta.localeConfig.getMessage("magenta.shop.error.not.defined.slot"),
                            Placeholder.parsed("category", category)
                        )
                    )

                if (!magenta.creditShopConfig.getConfig().contains("shop.categories.$category.icon"))
                    return player.sendMessage(
                        ModernText.miniModernText(
                            magenta.localeConfig.getMessage("magenta.shop.error.not.defined.icon"),
                            Placeholder.parsed("category", category)
                        )
                    )

                if (magenta.creditShopConfig.getConfig().getString("shop.categories.$category.icon")
                        .equals(material.name, ignoreCase = true)
                ) {
                    val glowing = magenta.creditShopConfig.getConfig().getBoolean("shop.categories.$category.glowing")
                    val name = magenta.creditShopConfig.getConfig().getString("shop.categories.$category.name").toString()

                    val item = ItemBuilder.from(
                        magenta.itemFactory.shopItem(material, name, glowing)
                    ).asGuiItem { action ->
                        if (action.isRightClick || action.isLeftClick) {
                            openCategory(player, category)
                        }
                        action.isCancelled = true
                    }
                    gui.setItem(magenta.creditShopConfig.getConfig().getInt("shop.categories.$category.slot"), item)
                }
            }
        }
        gui.open(player)
    }


    fun openCategory(player: Player, type: String) {
        val shopCategory = ShopConfig(magenta, "creditshop/categories/$type.yml")
        if (!shopCategory.fileExist())
            return player.sendMessage(
                ModernText.miniModernText(
                    magenta.localeConfig.getMessage("magenta.command.shop.error.category.not.exist"),
                    Placeholder.parsed("category", type)
                )
            )

        if (!player.hasPermission("magenta.credit.shop.category.$type") || !player.hasPermission("magenta.credit.shop.category.*"))
            return player.sendMessage(
                ModernText.miniModernText(
                    magenta.localeConfig.getMessage("magenta.command.shop.error.category.permission"),
                    Placeholder.parsed("category", type)
                )
            )

        val name = magenta.creditShopConfig.getConfig().getString("shop.gui.categoryName").toString()
        val categoryName = magenta.creditShopConfig.getConfig().getString("shop.categories.$type.name").toString()

        val gui: PaginatedGui = shopUI.paginatedGui(
            ModernText.miniModernText(name, Placeholder.parsed("category", categoryName)),
            shopCategory.getConfig().getInt("shop.gui.size", 6)
        )

        if (shopCategory.getConfig().contains("shop.gui.fill")) {
            shopUI.fillBorder(gui.filler, shopCategory.getConfig())
            shopUI.fillTop(gui.filler, shopCategory.getConfig())
            shopUI.fillBottom(gui.filler, shopCategory.getConfig())
            shopUI.fillAll(gui.filler, shopCategory.getConfig())
        }

        if (shopCategory.getConfig().contains("shop.custom-items")) {
            for (item in shopCategory.getConfig().getConfigurationSection("shop.custom-items")?.getKeys(false)!!) {
                val material = Material.getMaterial(shopCategory.getConfig().getString("shop.custom-items.$item.icon").toString())
                if (material != null) {
                    if (!shopCategory.getConfig().contains("shop.custom-items.$item.name"))
                        return player.sendMessage(
                            ModernText.miniModernText(
                                magenta.localeConfig.getMessage("magenta.shop.error.not.defined.name"),
                                Placeholder.parsed("category", type)
                            )
                        )
                    if (!shopCategory.getConfig().contains("shop.custom-items.$item.position.slot"))
                        return player.sendMessage(
                            ModernText.miniModernText(
                                magenta.localeConfig.getMessage("magenta.shop.error.not.defined.slot"),
                                Placeholder.parsed("category", type)
                            )
                        )
                    val itemName = shopCategory.getConfig().getString("shop.custom-items.$item.name").toString()
                    val slot = shopCategory.getConfig().getInt("shop.custom-items.$item.position.slot")
                    val glowing = shopCategory.getConfig().getBoolean("shop.custom-items.$item.glowing")
                    val lore = shopCategory.getConfig().getStringList("shop.custom-items.$item.lore")
                    val guiItem = ItemBuilder.from(magenta.itemFactory.item(material, itemName, lore, glowing)).asGuiItem {
                        return@asGuiItem
                    }
                    gui.setItem(slot, guiItem)
                }
            }
        }

        if (shopCategory.getConfig().contains("shop.items")) {
            for (item in shopCategory.getConfig().getConfigurationSection("shop.items")?.getKeys(false)!!) {
                val material =
                    Material.getMaterial(shopCategory.getConfig().getString("shop.items.$item.icon").toString())
                if (material != null) {
                    if (shopCategory.getConfig().contains("shop.items.$item")) {

                        if (!shopCategory.getConfig().contains("shop.items.$item.name"))
                            return player.sendMessage(
                                ModernText.miniModernText(
                                    magenta.localeConfig.getMessage("magenta.shop.error.not.defined.name"),
                                    Placeholder.parsed("category", type)
                                )
                            )

                        if (!shopCategory.getConfig().contains("shop.items.$item.position.slot"))
                            return player.sendMessage(
                                ModernText.miniModernText(
                                    magenta.localeConfig.getMessage("magenta.shop.error.not.defined.slot"),
                                    Placeholder.parsed("category", type)
                                )
                            )

                        if (!shopCategory.getConfig().contains("shop.items.$item.commands"))
                            return player.sendMessage(
                                ModernText.miniModernText(
                                    magenta.localeConfig.getMessage("magenta.shop.error.not.defined.commands"),
                                    Placeholder.parsed("category", type)
                                )
                            )

                        if (!shopCategory.getConfig().contains("shop.items.$item.buy.quantity"))
                            return player.sendMessage(
                                ModernText.miniModernText(
                                    magenta.localeConfig.getMessage("magenta.shop.error.not.defined.quantity"),
                                    Placeholder.parsed("category", type)
                                )
                            )

                        val itemName = shopCategory.getConfig().getString("shop.items.$item.name").toString()
                        val slot = shopCategory.getConfig().getInt("shop.items.$item.position.slot")
                        val glowing = shopCategory.getConfig().getBoolean("shop.items.$item.glowing")
                        val buyPrice = shopCategory.getConfig().getDouble("shop.items.$item.buy.price")
                        val quantity = shopCategory.getConfig().getInt("shop.items.$item.buy.quantity")
                        val commands = shopCategory.getConfig().getStringList("shop.items.$item.commands")

                        val isBuyAllowed = shopCategory.getConfig().contains("shop.items.$item.buy.price")

                        val guiItem = ItemBuilder.from(
                            magenta.itemFactory.creditShopItem(
                                player,
                                material,
                                itemName,
                                quantity,
                                buyPrice,
                                glowing,
                                magenta.creditShopConfig.getConfig().getStringList("shop.gui.item_lore")
                            )
                        ).asGuiItem()

                        guiItem.setAction { action ->
                            if (action.isLeftClick) {
                                creditShopInventory.buyItem(
                                    action,
                                    guiItem.itemStack.displayName(),
                                    buyPrice,
                                    quantity,
                                    commands,
                                    "magenta.shop.success.buy",
                                    isBuyAllowed
                                )
                                return@setAction
                            }
                        }
                        gui.setItem(slot, guiItem)
                    }
                }
            }
        }
        for (material in Material.entries) {
            shopUI.nextPage(player, material, shopCategory, "previous", gui)
            shopUI.closeButton(
                player,
                material,
                gui,
                shopCategory,
                this
            )
            shopUI.nextPage(player, material, shopCategory, "next", gui)
        }
        gui.open(player)
    }


}