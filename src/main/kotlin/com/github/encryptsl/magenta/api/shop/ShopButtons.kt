package com.github.encryptsl.magenta.api.shop

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.ShopConfig
import dev.triumphteam.gui.builder.item.ItemBuilder
import dev.triumphteam.gui.guis.PaginatedGui
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

object ShopButtons {

    @JvmStatic
    fun paginationButton(player: Player, material: Material, gui: PaginatedGui, shopCategory: ShopConfig, btnType: String,magenta:Magenta) {
        if (shopCategory.getConfig().contains("shop.gui.button.$btnType")) {
            if (!shopCategory.getConfig().contains("shop.gui.button.$btnType.positions"))
                return player.sendMessage(magenta.localeConfig.getMessage("magenta.shop.error.button.missing.positions"))

            if (!shopCategory.getConfig().contains("shop.gui.button.$btnType.positions.row"))
                return player.sendMessage(magenta.localeConfig.getMessage("magenta.shop.error.button.missing.positions.row"))

            if (!shopCategory.getConfig().contains("shop.gui.button.$btnType.positions.col"))
                return player.sendMessage(magenta.localeConfig.getMessage("magenta.shop.error.button.missing.positions.col"))

            if (shopCategory.getConfig().getString("shop.gui.button.$btnType.item").equals(material.name, true)) {
                gui.setItem(shopCategory.getConfig().getInt("shop.gui.button.$btnType.positions.row"), shopCategory.getConfig().getInt("shop.gui.button.$btnType.positions.col"), ItemBuilder.from(material)
                    .setName(shopCategory.getConfig().getString("shop.gui.button.$btnType.name").toString())
                    .asGuiItem { event: InventoryClickEvent -> gui.next() })
            }
        }
    }

    @JvmStatic
    fun closeButton(player: Player, material: Material, gui: PaginatedGui, shopCategory: ShopConfig, action: String, magenta: Magenta, shopManager: ShopManager) {
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
                gui.setItem(shopCategory.getConfig().getInt("shop.gui.button.close.positions.row"), shopCategory.getConfig().getInt("shop.gui.button.close.positions.col"), ItemBuilder.from(material)
                    .setName(shopCategory.getConfig().getString("shop.gui.button.close.name").toString())
                    .asGuiItem { event: InventoryClickEvent -> if (action == "back") shopManager.openShop(player) else gui.close(player) })
            }
        }
    }
}