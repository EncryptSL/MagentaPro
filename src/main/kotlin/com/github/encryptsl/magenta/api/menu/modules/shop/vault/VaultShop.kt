package com.github.encryptsl.magenta.api.menu.modules.shop.vault

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.kmono.lib.api.config.UniversalConfig
import com.github.encryptsl.kmono.lib.api.economy.models.EconomyDataPayment
import com.github.encryptsl.kmono.lib.utils.ItemCreator
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.MenuUI
import com.github.encryptsl.magenta.api.menu.components.template.Menu
import com.github.encryptsl.magenta.api.menu.modules.shop.Product
import com.github.encryptsl.magenta.common.Permissions
import com.github.encryptsl.magenta.common.model.ShopManager
import dev.triumphteam.gui.builder.item.ItemBuilder
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.math.BigDecimal

class VaultShop(private val magenta: Magenta) : Menu {

    private val vaultShopPaymentMethods: VaultShopPaymentMethods by lazy { VaultShopPaymentMethods(magenta) }
    private val menuUI: MenuUI by lazy { MenuUI(magenta) }
    val shopManager: ShopManager by lazy { ShopManager(magenta) }

    override fun open(player: Player) {
        val rows = magenta.shopConfig.getConfig().getInt("menu.gui.size", 6)
        val gui = menuUI.simpleBuilderGui(rows,
            ModernText.miniModernText(magenta.shopConfig.getConfig().getString("menu.gui.display").toString()),
            magenta.shopConfig.getConfig()
        )
        menuUI.useAllFillers(gui, magenta.shopConfig.getConfig())
        for (category in shopManager.getShopCategories()) {
            val material = Material.entries.firstOrNull {
                el -> el.name.equals(magenta.shopConfig.getConfig().getString("menu.categories.$category.icon").toString(), true)
            } ?: continue

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
                ItemCreator(material, 1).setName(ModernText.miniModernText(name)).create()
            ).asGuiItem { context ->
                return@asGuiItem openCategory(context.whoClicked as Player, category)
            }
            gui.setItem(magenta.shopConfig.getConfig().getInt("menu.categories.$category.slot"), item)
        }
        gui.open(player)
    }

    fun openCategory(player: Player, type: String) {
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
       val rows = shopCategory.getConfig().getInt("menu.gui.size", 6)

       val gui = menuUI.paginatedBuilderGui(rows,
           ModernText.miniModernText(name, Placeholder.parsed("category", categoryName)),
           shopCategory.getConfig()
       )

       val products = shopManager.getShopProducts(shopCategory.getConfig()).iterator()

        menuUI.useAllFillers(gui, shopCategory.getConfig())

        while (products.hasNext()) {
            val product = products.next()

            val itemName = product.name ?: product.material.name
            val material = product.material
            val buyPrice = product.buyPrice
            val sellPrice = product.sellPrice
            val isBuyAllowed = product.isBuyAllowed
            val isSellAllowed = product.isSellAllowed
            val commands = product.commands

            val itemStack = getShopProduct(itemName, material, buyPrice, sellPrice, isBuyAllowed, isSellAllowed)

            val guiItem = ItemBuilder.from(itemStack).asGuiItem { context ->
                // BUY BY STACK = 64
                if (context.isShiftClick && context.isLeftClick) {
                    return@asGuiItem buy(context.whoClicked as Player, material, buyPrice, 64, itemName, commands, isBuyAllowed)
                }
                // BUY BY ONE ITEM = 1
                if (context.isLeftClick) {
                    return@asGuiItem buy(context.whoClicked as Player, material, buyPrice, 1, itemName, commands, isBuyAllowed)
                }
                // SELL BY STACK = 64
                if (context.isShiftClick && context.isRightClick) {
                    val itemFromInv = context.whoClicked.inventory.storageContents.firstOrNull { el -> el?.type == material }
                    itemFromInv?.let { return@asGuiItem sell(context.whoClicked as Player, material, sellPrice, it.amount, itemName, commands, isSellAllowed) }
                }
                // SELL BY ONE ITEM = 1
                if (context.isRightClick) {
                    return@asGuiItem sell(context.whoClicked as Player, material, sellPrice, 1, itemName, commands, isSellAllowed)
                }
            }
            gui.addItem(guiItem)
        }

        menuUI.pagination(player, gui, shopCategory.getConfig(), this)
        gui.open(player)
   }

   private fun buy(
       player: Player,
       material: Material,
       buyPrice: BigDecimal,
       counts: Int,
       itemName: String,
       commands: List<String>,
       isBuyAllowed: Boolean
   ) {

       val itemStack = ItemCreator(material, counts).setName(ModernText.miniModernText(itemName)).create()

       vaultShopPaymentMethods.buy(player, EconomyDataPayment(
           Product(itemStack, itemStack.displayName(), buyPrice.times(counts.toBigDecimal()), counts)), isBuyAllowed, commands)
   }


   private fun sell(
       player: Player,
       material: Material,
       sellPrice: BigDecimal,
       counts: Int,
       itemName: String,
       commands: List<String>,
       isSellAllowed: Boolean
   ) {
       val itemStack = ItemCreator(material, counts).setName(ModernText.miniModernText(itemName)).create()

       vaultShopPaymentMethods.sell(player, EconomyDataPayment(
           Product(itemStack, itemStack.displayName(), sellPrice.times(counts.toBigDecimal()), counts)), isSellAllowed, commands)
   }

   private fun getShopProduct(
       itemName: String,
       material: Material,
       buyPrice: BigDecimal,
       sellPrice: BigDecimal,
       isBuyAllowed: Boolean,
       isSellAllowed: Boolean
   ): ItemStack {
       return magenta.itemFactory.shopItem(
           itemName,
           material,
           buyPrice,
           sellPrice,
           isBuyAllowed,
           isSellAllowed,
           magenta.shopConfig.getConfig()
       )
   }
}