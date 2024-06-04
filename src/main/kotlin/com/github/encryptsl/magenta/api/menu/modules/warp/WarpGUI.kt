package com.github.encryptsl.magenta.api.menu.modules.warp

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.kmono.lib.utils.ItemBuilder
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.MenuUI
import com.github.encryptsl.magenta.api.menu.components.template.Menu
import dev.triumphteam.gui.guis.BaseGui
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class WarpGUI(private val magenta: Magenta) : Menu {


    private val menuUI: MenuUI by lazy { MenuUI(magenta) }
    private val warpPlayerGUI: WarpPlayerGUI by lazy { WarpPlayerGUI(magenta, this, WarpPlayerEditorGUI(magenta)) }

    override fun open(player: Player) {
        val rows = magenta.homeMenuConfig.getConfig().getInt("menu.gui.size", 6)
        val warps = magenta.warpModel.getWarps().join()

        val gui = menuUI.paginatedBuilderGui(rows,
            ModernText.miniModernText(magenta.warpMenuConfig.getConfig().getString("menu.gui.display").toString(),
                Placeholder.parsed("count", warps.count().toString())),
            magenta.homeMenuConfig.getConfig()
        )

        menuUI.useAllFillers(gui, magenta.warpMenuConfig.getConfig())

        if (!magenta.warpMenuConfig.getConfig().contains("menu.warp-info.display")) return
        if (!magenta.warpMenuConfig.getConfig().contains("menu.warp-info.lore")) return

        for (warp in warps) {
            val material = Material.getMaterial(warp.warpIcon) ?: Material.OAK_SIGN

            val itemComponentName = ModernText.miniModernText(magenta.warpMenuConfig.getConfig().getString("menu.warp-info.display").toString(),
                Placeholder.parsed("warp", warp.warpName)
            )

            val lore = magenta.warpMenuConfig.getConfig()
                .getStringList("menu.warp-info.lore")
                .map { ModernText.miniModernText(it, TagResolver.resolver(
                    Placeholder.parsed("owner", warp.owner),
                    Placeholder.parsed("warp", warp.warpName),
                    Placeholder.parsed("world", warp.world),
                    Placeholder.parsed("x", warp.x.toString()),
                    Placeholder.parsed("y", warp.y.toString()),
                    Placeholder.parsed("z", warp.z.toString()),
                    Placeholder.parsed("yaw", warp.yaw.toString()),
                    Placeholder.parsed("pitch", warp.pitch.toString()),
                    )) }

            gui.addItem(
                dev.triumphteam.gui.builder.item.ItemBuilder.from(
                    ItemBuilder(material, 1).setName(itemComponentName).addLore(lore.toMutableList()).create()
                ).asGuiItem { context ->
                    if (context.isLeftClick || context.isRightClick) {
                        context.whoClicked.teleport(magenta.warpModel.toLocation(warp.warpName))
                    }
                }
            )
            gui.update()
        }

        menuUI.pagination(player, gui, magenta.warpMenuConfig.getConfig(), null)
        actionCustomButtons(player, gui, magenta.warpMenuConfig.getConfig())
        gui.open(player)
    }


    private fun actionCustomButtons(
        player: Player, gui: BaseGui, config: FileConfiguration
    ) {
        for (el in config.getConfigurationSection("menu.items.buttons")?.getKeys(false)!!) {
            val material = Material.getMaterial(config.getString("menu.items.buttons.${el}.icon").toString()) ?: continue
            if (config.contains("menu.items.buttons.$el")) {
                if (!config.contains("menu.items.buttons.$el.name"))
                    return player.sendMessage(magenta.locale.translation("magenta.menu.error.not.defined.name",
                        Placeholder.parsed("category", config.name)
                    ))

                val lores = config
                    .getStringList("menu.items.buttons.$el.lore")
                    .map { ModernText.miniModernText(it) }

                val itemStack = ItemBuilder(material, 1)
                    .setName(ModernText.miniModernText(config.getString("menu.items.buttons.${el}.name").toString()))
                    .addLore(lores.toMutableList()).create()

                val actionItem = getItem(itemStack, config, el)

                gui.setItem(config.getInt("menu.items.buttons.$el.positions.row"), config.getInt("menu.items.buttons.$el.positions.col"), actionItem)
            }
        }
    }

    private fun getItem(
        itemStack: ItemStack,
        config: FileConfiguration,
        el: String
    ): dev.triumphteam.gui.guis.GuiItem {
        return dev.triumphteam.gui.builder.item.ItemBuilder.from(itemStack).asGuiItem { context ->
            if (context.isLeftClick) {
                openOwnerWarps(context.whoClicked as Player, config, el)
            }
        }
    }

    private fun openOwnerWarps(
        player: Player,
        fileConfiguration: FileConfiguration,
        el: String
    ) {
        if (fileConfiguration.getString("menu.items.buttons.$el.action").equals("OPEN_MENU", true)) {
            warpPlayerGUI.open(player)
        }
    }

}