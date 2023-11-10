package com.github.encryptsl.magenta.api.shop.helpers

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.config.ShopConfig
import com.github.encryptsl.magenta.api.shop.credits.CreditShop
import com.github.encryptsl.magenta.api.shop.vault.VaultShop
import com.github.encryptsl.magenta.common.utils.ModernText
import dev.triumphteam.gui.builder.item.ItemBuilder
import dev.triumphteam.gui.components.GuiType
import dev.triumphteam.gui.components.util.GuiFiller
import dev.triumphteam.gui.guis.Gui
import dev.triumphteam.gui.guis.GuiItem
import dev.triumphteam.gui.guis.PaginatedGui
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

class ShopUI(private val magenta: Magenta) : ShopItems {

    override fun simpleGui(title: String, size: Int, guiType: GuiType): Gui {
        return Gui.gui()
            .title(ModernText.miniModernText(title))
            .type(guiType)
            .rows(size)
            .disableItemPlace()
            .disableItemTake()
            .disableItemDrop()
            .disableItemSwap()
            .create()
    }

    override fun paginatedGui(title: Component, size: Int): PaginatedGui {
        return Gui.paginated()
            .title(title)
            .rows(size)
            .disableItemPlace()
            .disableItemTake()
            .disableItemDrop()
            .disableItemSwap()
            .create()
    }

    override fun fillBorder(
        guiFiller: GuiFiller,
        fileConfiguration: FileConfiguration
    ) {
        if (fileConfiguration.contains("shop.gui.fill.border")) {
            guiFiller.fillBorder(
                GuiItem(
                    Material.valueOf(
                        fileConfiguration.getString("shop.gui.fill.border").toString()
                    )
                )
            )
            return
        }
    }

    override fun fillTop(
        guiFiller: GuiFiller,
        fileConfiguration: FileConfiguration
    ) {
        if (fileConfiguration.contains("shop.gui.fill.top")) {
            guiFiller.fillBorder(
                GuiItem(
                    Material.valueOf(
                        fileConfiguration.getString("shop.gui.fill.top").toString()
                    )
                )
            )
            return
        }
    }

    override fun fillBottom(
        guiFiller: GuiFiller,
        fileConfiguration: FileConfiguration
    ) {
        if (fileConfiguration.contains("shop.gui.fill.bottom")) {
            guiFiller.fillBorder(
                GuiItem(
                    Material.valueOf(
                        fileConfiguration.getString("shop.gui.fill.bottom").toString()
                    )
                )
            )
            return
        }
    }

    override fun fillAll(
        guiFiller: GuiFiller,
        fileConfiguration: FileConfiguration
    ) {
        if (fileConfiguration.contains("shop.gui.fill.all")) {
            guiFiller.fillBorder(
                GuiItem(
                    Material.valueOf(
                        fileConfiguration.getString("shop.gui.fill.all").toString()
                    )
                )
            )
            return
        }
    }

    override fun previousPage(
        player: Player,
        material: Material,
        shopCategory: ShopConfig,
        btnType: String,
        gui: PaginatedGui
    ) {
        if (shopCategory.getConfig().contains("shop.gui.button.$btnType")) {
            if (!shopCategory.getConfig().contains("shop.gui.button.$btnType.positions"))
                return player.sendMessage(magenta.localeConfig.getMessage("magenta.shop.error.button.missing.positions"))

            if (!shopCategory.getConfig().contains("shop.gui.button.$btnType.positions.row"))
                return player.sendMessage(magenta.localeConfig.getMessage("magenta.shop.error.button.missing.positions.row"))

            if (!shopCategory.getConfig().contains("shop.gui.button.$btnType.positions.col"))
                return player.sendMessage(magenta.localeConfig.getMessage("magenta.shop.error.button.missing.positions.col"))

            if (shopCategory.getConfig().getString("shop.gui.button.$btnType.item").equals(material.name, true)) {
                gui.setItem(shopCategory.getConfig().getInt("shop.gui.button.$btnType.positions.row"),
                    shopCategory.getConfig().getInt("shop.gui.button.$btnType.positions.col"),
                    ItemBuilder.from(magenta.itemFactory.shopItem(material, shopCategory.getConfig().getString("shop.gui.button.$btnType.name").toString()))
                        .asGuiItem { gui.previous() }
                )
            }
        }
    }

    override fun nextPage(
        player: Player,
        material: Material,
        shopCategory: ShopConfig,
        btnType: String,
        gui: PaginatedGui
    ) {
        if (shopCategory.getConfig().contains("shop.gui.button.$btnType")) {
            if (!shopCategory.getConfig().contains("shop.gui.button.$btnType.positions"))
                return player.sendMessage(magenta.localeConfig.getMessage("magenta.shop.error.button.missing.positions"))

            if (!shopCategory.getConfig().contains("shop.gui.button.$btnType.positions.row"))
                return player.sendMessage(magenta.localeConfig.getMessage("magenta.shop.error.button.missing.positions.row"))

            if (!shopCategory.getConfig().contains("shop.gui.button.$btnType.positions.col"))
                return player.sendMessage(magenta.localeConfig.getMessage("magenta.shop.error.button.missing.positions.col"))

            if (shopCategory.getConfig().getString("shop.gui.button.$btnType.item").equals(material.name, true)) {
                gui.setItem(shopCategory.getConfig().getInt("shop.gui.button.$btnType.positions.row"),
                    shopCategory.getConfig().getInt("shop.gui.button.$btnType.positions.col"),
                    ItemBuilder.from(magenta.itemFactory.shopItem(material, shopCategory.getConfig().getString("shop.gui.button.$btnType.name").toString()))
                        .asGuiItem { gui.next() }
                )
            }
        }
    }

    override fun closeButton(
        player: Player,
        material: Material,
        gui: PaginatedGui,
        shopCategory: ShopConfig,
        vaultShop: VaultShop
    ) {
        if (shopCategory.getConfig().contains("shop.gui.button.close")) {
            if (!shopCategory.getConfig().contains("shop.gui.button.close.positions"))
                return player.sendMessage(magenta.localeConfig.getMessage("magenta.shop.error.button.missing.positions"))

            if (!shopCategory.getConfig().contains("shop.gui.button.close.positions.row"))
                return player.sendMessage(magenta.localeConfig.getMessage("magenta.shop.error.button.missing.positions.row"))

            if (!shopCategory.getConfig().contains("shop.gui.button.close.positions.col"))
                return player.sendMessage(magenta.localeConfig.getMessage("magenta.shop.error.button.missing.positions.col"))

            if (!shopCategory.getConfig().contains("shop.gui.button.close.action"))
                return player.sendMessage(magenta.localeConfig.getMessage("magenta.shop.error.button.missing.action"))

            if (shopCategory.getConfig().getString("shop.gui.button.close.item").equals(material.name, true)) {
                gui.setItem(shopCategory.getConfig().getInt("shop.gui.button.close.positions.row"),
                    shopCategory.getConfig().getInt("shop.gui.button.close.positions.col"),
                    ItemBuilder.from(magenta.itemFactory.shopItem(material, shopCategory.getConfig().getString("shop.gui.button.close.name").toString()))
                        .asGuiItem {
                            if (shopCategory.getConfig().getString("shop.gui.button.close.action")?.contains("back") == true) vaultShop.openShop(player) else gui.close(
                                player
                            )
                        })
            }
        }
    }

    override fun closeButton(
        player: Player,
        material: Material,
        gui: PaginatedGui,
        shopCategory: ShopConfig,
        creditShop: CreditShop
    ) {
        if (shopCategory.getConfig().contains("shop.gui.button.close")) {
            if (!shopCategory.getConfig().contains("shop.gui.button.close.positions"))
                return player.sendMessage(magenta.localeConfig.getMessage("magenta.shop.error.button.missing.positions"))

            if (!shopCategory.getConfig().contains("shop.gui.button.close.positions.row"))
                return player.sendMessage(magenta.localeConfig.getMessage("magenta.shop.error.button.missing.positions.row"))

            if (!shopCategory.getConfig().contains("shop.gui.button.close.positions.col"))
                return player.sendMessage(magenta.localeConfig.getMessage("magenta.shop.error.button.missing.positions.col"))

            if (!shopCategory.getConfig().contains("shop.gui.button.close.action"))
                return player.sendMessage(magenta.localeConfig.getMessage("magenta.shop.error.button.missing.action"))

            if (shopCategory.getConfig().getString("shop.gui.button.close.item").equals(material.name, true)) {
                gui.setItem(shopCategory.getConfig().getInt("shop.gui.button.close.positions.row"),
                    shopCategory.getConfig().getInt("shop.gui.button.close.positions.col"),
                    ItemBuilder.from(magenta.itemFactory.shopItem(material, shopCategory.getConfig().getString("shop.gui.button.close.name").toString()))
                        .asGuiItem {
                            if (shopCategory.getConfig().getString("shop.gui.button.close.action")?.contains("back") == true) creditShop.openShop(player) else gui.close(
                                player
                            )
                        })
            }
        }
    }
}