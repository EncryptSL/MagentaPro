package com.github.encryptsl.magenta.api.menu.modules.shop.vault

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.kmono.lib.api.config.UniversalConfig
import com.github.encryptsl.kmono.lib.api.economy.models.EconomyDataPayment
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.MenuUI
import com.github.encryptsl.magenta.api.menu.components.template.Menu
import com.github.encryptsl.magenta.api.menu.modules.shop.Product
import com.github.encryptsl.magenta.common.Permissions
import com.github.encryptsl.magenta.common.model.ShopManager
import dev.triumphteam.gui.paper.Gui
import dev.triumphteam.gui.paper.builder.item.ItemBuilder
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class VaultShop(private val magenta: Magenta) : Menu {

    private val vaultShopPaymentMethods: VaultShopPaymentMethods by lazy { VaultShopPaymentMethods(magenta) }
    private val menuUI: MenuUI by lazy { MenuUI(magenta) }
    val shopManager: ShopManager by lazy { ShopManager(magenta) }

    override fun open(player: Player) {
        val rows = magenta.shopConfig.getConfig().getInt("menu.gui.size", 6)
        val gui = Gui.of(rows).title(
            ModernText.miniModernText(magenta.shopConfig.getConfig().getString("menu.gui.display").toString())
        )

        gui.component { component ->
            component.render { container, viewer ->
                menuUI.useAllFillers(rows, container, magenta.shopConfig.getConfig())
                for (category in shopManager.getShopCategories()) {
                    val material = Material.getMaterial(
                        magenta.shopConfig.getConfig().getString("menu.categories.$category.icon").toString()
                    ) ?: continue

                    if (!magenta.shopConfig.getConfig().contains("menu.categories.$category.name"))
                        return@render viewer.sendMessage(magenta.locale.translation("magenta.menu.error.not.defined.name",
                            Placeholder.parsed("category", category)
                        ))

                    if (!magenta.shopConfig.getConfig().contains("menu.categories.$category.slot"))
                        return@render viewer.sendMessage(magenta.locale.translation("magenta.menu.error.not.defined.slot",
                            Placeholder.parsed("category", category)
                        ))

                    if (!magenta.shopConfig.getConfig().contains("menu.categories.$category.icon"))
                        return@render viewer.sendMessage(magenta.locale.translation("magenta.menu.error.not.defined.icon",
                            Placeholder.parsed("category", category)
                        ))

                    val name = magenta.shopConfig.getConfig().getString("menu.categories.$category.name").toString()
                    val item = ItemBuilder.from(
                        magenta.itemFactory.shopItem(material, name)
                    ).asGuiItem { whoClick, _ ->
                        return@asGuiItem openCategory(whoClick, category)
                    }
                    container.set(magenta.shopConfig.getConfig().getInt("menu.categories.$category.slot"), item)
                }
            }
        }.build().open(player)
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

       val gui = Gui.of(
           rows
       ).title(
           ModernText.miniModernText(name, Placeholder.parsed("category", categoryName))
       )

       val products = shopManager.getShopProducts(shopCategory.getConfig())

       gui.component { component -> component.render { container, _ ->
           menuUI.useAllFillers(rows, container, shopCategory.getConfig())

           for (product in products.withIndex()) {
               val itemName = product.value.name ?: product.value.material.name
               val material = product.value.material
               val buyPrice = product.value.buyPrice
               val sellPrice = product.value.sellPrice
               val isBuyAllowed = product.value.isBuyAllowed
               val isSellAllowed = product.value.isSellAllowed
               val commands = product.value.commands

               val itemStack = getShopProduct(itemName, material, buyPrice, sellPrice, isBuyAllowed, isSellAllowed)

               val guiItem = ItemBuilder.from(itemStack).asGuiItem { _, _ ->
                   /*
                   // BUY BY STACK = 64
                   if (context.isShiftClick && context.isLeftClick) {
                       return@asGuiItem buy(player, material, buyPrice, 64, itemName, commands, isBuyAllowed)
                   }

                   // BUY BY ONE ITEM = 1
                   if (context.isLeftClick) {
                       return@asGuiItem buy(player, material, buyPrice, 1, itemName, commands, isBuyAllowed)
                   }

                   // SELL BY STACK = 64
                   if (context.isShiftClick && context.isRightClick) {
                       val itemFromInv = player.inventory.storageContents.filter { el -> el?.type == material }.first()
                       itemFromInv?.let { return@asGuiItem sell(player, material, sellPrice, it.amount, itemName, commands, isSellAllowed) }
                   }

                   // SELL BY ONE ITEM = 1
                   if (context.isRightClick) {
                       return@asGuiItem sell(player, material, sellPrice, 1, itemName, commands, isSellAllowed)
                   }
                   context.isCancelled = true
                   */
               }
               container.set(product.index, guiItem)
           }
       }}.build().open(player)
   }

   private fun buy(
       player: Player,
       material: Material,
       buyPrice: Double,
       counts: Int,
       itemName: String,
       commands: List<String>,
       isBuyAllowed: Boolean
   ) {

       val itemStack = magenta.itemFactory.shopItem(material, counts, itemName)

       vaultShopPaymentMethods.buy(player, EconomyDataPayment(
           Product(itemStack, itemStack.displayName(), buyPrice, counts)), isBuyAllowed, commands)
   }


   private fun sell(
       player: Player,
       material: Material,
       sellPrice: Double,
       counts: Int,
       itemName: String,
       commands: List<String>,
       isSellAllowed: Boolean
   ) {
       val itemStack = magenta.itemFactory.shopItem(material, counts, itemName)

       vaultShopPaymentMethods.sell(player, EconomyDataPayment(
           Product(itemStack, itemStack.displayName(), sellPrice, counts)), isSellAllowed, commands)
   }

   private fun getShopProduct(
       itemName: String,
       material: Material,
       buyPrice: Double,
       sellPrice: Double,
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