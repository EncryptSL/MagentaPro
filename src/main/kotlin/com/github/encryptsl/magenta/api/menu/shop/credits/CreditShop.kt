package com.github.encryptsl.magenta.api.menu.shop.credits

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.config.UniversalConfig
import com.github.encryptsl.magenta.api.menu.MenuUI
import com.github.encryptsl.magenta.common.hook.creditlite.CreditLiteHook
import com.github.encryptsl.magenta.common.utils.ModernText
import dev.triumphteam.gui.builder.item.ItemBuilder
import dev.triumphteam.gui.components.GuiType
import dev.triumphteam.gui.guis.Gui
import dev.triumphteam.gui.guis.PaginatedGui
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

class CreditShop(private val magenta: Magenta) {

    private val creditLiteHook: CreditLiteHook by lazy { CreditLiteHook(magenta) }
    private val creditShopInventory: CreditShopInventory by lazy { CreditShopInventory(magenta, creditLiteHook) }
    private val menuUI: MenuUI by lazy { MenuUI(magenta) }

    fun openShop(player: Player) {
        val gui: Gui = menuUI.simpleGui(
            magenta.creditShopConfig.getConfig().getString("menu.gui.display").toString(),
            magenta.creditShopConfig.getConfig().getInt("menu.gui.size", 6),
            GuiType.CHEST
        )

        menuUI.useAllFillers(gui.filler, magenta.creditShopConfig.getConfig())

        for (material in Material.entries) {
            for (category in magenta.creditShopConfig.getConfig().getConfigurationSection("menu.categories")
                ?.getKeys(false)!!) {

                if (!magenta.creditShopConfig.getConfig().contains("menu.categories.$category.name"))
                    return player.sendMessage(
                        ModernText.miniModernText(
                            magenta.localeConfig.getMessage("magenta.menu.error.not.defined.name"),
                            Placeholder.parsed("category", category)
                        )
                    )

                if (!magenta.creditShopConfig.getConfig().contains("menu.categories.$category.slot"))
                    return player.sendMessage(
                        ModernText.miniModernText(
                            magenta.localeConfig.getMessage("magenta.menu.error.not.defined.slot"),
                            Placeholder.parsed("category", category)
                        )
                    )

                if (!magenta.creditShopConfig.getConfig().contains("menu.categories.$category.icon"))
                    return player.sendMessage(
                        ModernText.miniModernText(
                            magenta.localeConfig.getMessage("magenta.menu.error.not.defined.icon"),
                            Placeholder.parsed("category", category)
                        )
                    )

                if (magenta.creditShopConfig.getConfig().getString("menu.categories.$category.icon")
                        .equals(material.name, ignoreCase = true)
                ) {
                    val glowing = magenta.creditShopConfig.getConfig().getBoolean("menu.categories.$category.glowing")
                    val name = magenta.creditShopConfig.getConfig().getString("menu.categories.$category.name").toString()

                    val item = ItemBuilder.from(
                        com.github.encryptsl.magenta.api.ItemBuilder(material, 1).setName(ModernText.miniModernText(name)).setGlowing(glowing).create()
                    ).asGuiItem { action ->
                        if (action.isRightClick || action.isLeftClick) {
                            openCategory(player, category)
                        }
                        action.isCancelled = true
                    }
                    gui.setItem(magenta.creditShopConfig.getConfig().getInt("menu.categories.$category.slot"), item)
                }
            }
        }
        gui.open(player)
    }


    fun openCategory(player: Player, type: String) {
        val shopCategory = UniversalConfig(magenta, "menu/creditshop/categories/$type.yml")
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

        val name = magenta.creditShopConfig.getConfig().getString("menu.gui.categoryName").toString()
        val categoryName = magenta.creditShopConfig.getConfig().getString("menu.categories.$type.name").toString()

        val gui: PaginatedGui = menuUI.paginatedGui(
            ModernText.miniModernText(name, Placeholder.parsed("category", categoryName)),
            shopCategory.getConfig().getInt("menu.gui.size", 6)
        )

        menuUI.useAllFillers(gui.filler, shopCategory.getConfig())

        if (shopCategory.getConfig().contains("menu.custom-items")) {
            customItems(player, categoryName, shopCategory.getConfig(), gui)
        }

        if (shopCategory.getConfig().contains("menu.items")) {
            for (item in shopCategory.getConfig().getConfigurationSection("menu.items")?.getKeys(false)!!) {
                val material = Material.getMaterial(shopCategory.getConfig().getString("menu.items.$item.icon").toString())
                if (material != null) {
                    if (shopCategory.getConfig().contains("menu.items.$item")) {

                        if (!shopCategory.getConfig().contains("menu.items.$item.name"))
                            return player.sendMessage(
                                ModernText.miniModernText(
                                    magenta.localeConfig.getMessage("magenta.menu.error.not.defined.name"),
                                    Placeholder.parsed("category", type)
                                )
                            )

                        if (!shopCategory.getConfig().contains("menu.items.$item.position.slot"))
                            return player.sendMessage(
                                ModernText.miniModernText(
                                    magenta.localeConfig.getMessage("magenta.menu.error.not.defined.slot"),
                                    Placeholder.parsed("category", type)
                                )
                            )

                        if (!shopCategory.getConfig().contains("menu.items.$item.commands"))
                            return player.sendMessage(
                                ModernText.miniModernText(
                                    magenta.localeConfig.getMessage("magenta.menu.error.not.defined.commands"),
                                    Placeholder.parsed("category", type)
                                )
                            )

                        if (!shopCategory.getConfig().contains("menu.items.$item.buy.quantity"))
                            return player.sendMessage(
                                ModernText.miniModernText(
                                    magenta.localeConfig.getMessage("magenta.menu.error.not.defined.quantity"),
                                    Placeholder.parsed("category", type)
                                )
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
                        val commands = shopCategory.getConfig().getStringList("menumenu.items.$item.commands")

                        val isBuyAllowed = shopCategory.getConfig().contains("menu.items.$item.buy.price")

                        val guiItem = ItemBuilder.from(
                            magenta.itemFactory.creditShopItem(
                                player,
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
        controlButtons(player, menuUI, shopCategory, gui)
        gui.open(player)
    }

    private fun controlButtons(player: Player, menuUI: MenuUI, shopConfig: UniversalConfig, paginatedGui: PaginatedGui) {
        for (material in Material.entries) {
            menuUI.nextPage(player, material, shopConfig.getConfig(), "previous", paginatedGui)
            menuUI.closeButton(
                player,
                material,
                paginatedGui,
                shopConfig.getConfig(),
                this
            )
            menuUI.nextPage(player, material, shopConfig.getConfig(), "next", paginatedGui)
        }
    }

    private fun customItems(player: Player, type: String, fileConfiguration: FileConfiguration, guiPaginatedGui: PaginatedGui) {
        for (item in fileConfiguration.getConfigurationSection("menu.custom-items")?.getKeys(false)!!) {
            val material = Material.getMaterial(fileConfiguration.getString("menu.custom-items.$item.icon").toString())
            if (material != null) {
                if (!fileConfiguration.contains("menu.custom-items.$item.name"))
                    return player.sendMessage(
                        ModernText.miniModernText(
                            magenta.localeConfig.getMessage("magenta.menu.error.not.defined.name"),
                            Placeholder.parsed("category", type)
                        )
                    )
                if (!fileConfiguration.contains("menu.custom-items.$item.position.slot"))
                    return player.sendMessage(
                        ModernText.miniModernText(
                            magenta.localeConfig.getMessage("magenta.menu.error.not.defined.slot"),
                            Placeholder.parsed("category", type)
                        )
                    )
                val itemName = fileConfiguration.getString("menu.custom-items.$item.name").toString()
                val slot = fileConfiguration.getInt("menu.custom-items.$item.position.slot")
                val glowing = fileConfiguration.getBoolean("menu.custom-items.$item.options.glowing")
                val lore = fileConfiguration.getStringList("menu.custom-items.$item.lore")
                val guiItem = ItemBuilder.from(magenta.itemFactory.item(material, itemName, lore, glowing)).asGuiItem {
                    return@asGuiItem
                }
                guiPaginatedGui.setItem(slot, guiItem)
            }
        }
    }


}