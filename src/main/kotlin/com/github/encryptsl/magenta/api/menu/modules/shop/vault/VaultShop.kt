package com.github.encryptsl.magenta.api.menu.modules.shop.vault

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.components.template.Menu
import com.github.encryptsl.magenta.common.model.ShopManager
import dev.triumphteam.gui.paper.Gui
import dev.triumphteam.gui.paper.builder.item.ItemBuilder
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Material
import org.bukkit.entity.Player

class VaultShop(private val magenta: Magenta) : Menu {

    private val vaultShopPaymentMethods: VaultShopPaymentMethods by lazy { VaultShopPaymentMethods(magenta) }
    val shopManager: ShopManager by lazy { ShopManager(magenta) }

    override fun open(player: Player) {
        val gui = Gui.of(magenta.shopConfig.getConfig().getInt("menu.gui.size", 6)).title(
            ModernText.miniModernText(magenta.shopConfig.getConfig().getString("menu.gui.display").toString())
        )

       //menuUI.useAllFillers(gui.filler, magenta.shopConfig.getConfig())

       //gui.setDefaultClickAction { el ->
       //    if (el.currentItem != null && el.isLeftClick || el.isRightClick) {
       //        paginationMenu.clickSound(el.whoClicked, magenta.shopConfig.getConfig())
       //    }
       //}

        gui.component { component ->
            component.render { container, viewer ->
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
                    ).asGuiItem { whoClick, context ->
                        //if (context.isRightClick || context.isLeftClick) {
                        //    return@asGuiItem openCategory(whoClick, category)
                        //}
                        //context.isCancelled = true
                    }
                    container.set(magenta.shopConfig.getConfig().getInt("menu.categories.$category.slot"), item)
                }
            }
        }.build().open(player)
    }


    /*
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

       val gui: PaginatedGui = paginationMenu.paginatedGui(ModernText.miniModernText(name,
           Placeholder.parsed("category", categoryName)
       ), shopCategory.getConfig().getInt("menu.gui.size", 6))

       menuUI.useAllFillers(gui.filler, shopCategory.getConfig())

       gui.setDefaultClickAction { el ->
           if (el.currentItem != null && el.isLeftClick || el.isRightClick || el.isShiftClick) {
               paginationMenu.clickSound(el.whoClicked, shopCategory.getConfig())
           }
       }

       val products = shopManager.getShopProducts(shopCategory)

       for (product in products) {
           val itemName = product.name ?: product.material

           val actionItem = getItem(itemName, product.material, product.buyPrice, product.sellPrice, product.isBuyAllowed, product.isSellAllowed)

           actionItem.setAction { action ->
               // BUY BY STACK = 64
               if (action.isShiftClick && action.isLeftClick) {
                   return@setAction buy(action, product.material, product.buyPrice, 64, itemName, product.isBuyAllowed, product.commands)
               }

               // BUY BY ONE ITEM = 1
               if (action.isLeftClick) {
                   return@setAction buy(action, product.material, product.buyPrice, 1, itemName, product.isBuyAllowed, product.product.commands)
               }

               // SELL BY STACK = 64
               if (action.isShiftClick && action.isRightClick) {
                   val itemFromInv = player.inventory.storageContents.filter { el -> el?.type == material }.first()
                   itemFromInv?.let { return@setAction sell(action, product.material, product.sellPrice, it.amount, itemName, product.isSellAllowed) }
               }

               // SELL BY ONE ITEM = 1
               if (action.isRightClick) {
                   return@setAction sell(action, product.material, product.sellPrice, 1, itemName, product.isSellAllowed)
               }
               action.isCancelled = true
           }
           gui.addItem(actionItem)
       }
       paginationMenu.paginatedControlButtons(player, shopCategory.getConfig(), gui)
       gui.open(player)
   }
   */

    /*
   private fun buy(
       action: InventoryClickEvent,
       material: Material,
       buyPrice: Double,
       counts: Int,
       itemName: String,
       isBuyAllowed: Boolean,
       commands: MutableList<String>?
   ) {
       vaultShopPaymentMethods.buy(
           EconomyDataPayment(
               magenta.itemFactory.shopItem(material, counts, itemName),
               ShopHelper.calcPrice(counts, buyPrice),
               isBuyAllowed
           ), commands, action)
   }*/

    /*
   private fun sell(
       action: InventoryClickEvent,
       material: Material,
       sellPrice: Double,
       counts: Int,
       itemName: String,
       isSellAllowed: Boolean
   ) {
       vaultShopPaymentMethods.sell(
           EconomyDataPayment(magenta.itemFactory.shopItem(material, counts, itemName),
               ShopHelper.calcPrice(counts, sellPrice), isSellAllowed)
           , action)
   }

   private fun getItem(
       itemName: String,
       material: Material,
       buyPrice: Double,
       sellPrice: Double,
       isBuyAllowed: Boolean,
       isSellAllowed: Boolean
   ): GuiItem {
       return ItemBuilder.from(
           magenta.itemFactory.shopItem(
               itemName,
               material,
               buyPrice,
               sellPrice,
               isBuyAllowed,
               isSellAllowed,
               magenta.shopConfig.getConfig()
           )
       ).asGuiItem()
   }*/
}