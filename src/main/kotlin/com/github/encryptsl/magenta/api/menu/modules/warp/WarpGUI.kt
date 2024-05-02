package com.github.encryptsl.magenta.api.menu.modules.warp

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.MenuUI
import com.github.encryptsl.magenta.api.menu.provider.templates.MenuExtender
import com.github.encryptsl.magenta.common.utils.ModernText
import dev.triumphteam.gui.builder.item.ItemBuilder
import dev.triumphteam.gui.guis.PaginatedGui
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.HumanEntity

class WarpGUI(private val magenta: Magenta) : MenuExtender {

    private val menuUI: MenuUI by lazy { MenuUI(magenta) }
    private val paginationMenu = menuUI.PaginationMenu(magenta, this)
    private val warpPlayerGUI: WarpPlayerGUI by lazy { WarpPlayerGUI(magenta, this, WarpPlayerEditorGUI(magenta)) }

    override fun openMenu(player: HumanEntity) {
        val gui = paginationMenu.paginatedGui(
            ModernText.miniModernText(magenta.warpMenuConfig.getConfig().getString("menu.gui.display").toString(), Placeholder.parsed("count", magenta.warpModel.getWarps().count().toString())),
            magenta.homeMenuConfig.getConfig().getInt("menu.gui.size", 6)
        )
        menuUI.useAllFillers(gui.filler, magenta.warpMenuConfig.getConfig())

        val warps = magenta.warpModel.getWarps()

        for (warp in warps) {
            val material = Material.getMaterial(warp.warpIcon) ?: Material.OAK_SIGN

            val itemHomeBuilder = com.github.encryptsl.magenta.api.ItemBuilder(material, 1)

            if (magenta.warpMenuConfig.getConfig().contains("menu.warp-info.display")) {
                itemHomeBuilder.setName(
                    ModernText.miniModernText(magenta.warpMenuConfig.getConfig().getString("menu.warp-info.display").toString(),
                        Placeholder.parsed("warp", warp.warpName)
                    ))
            }

            if (magenta.warpMenuConfig.getConfig().contains("menu.warp-info.lore")) {
                val lores = magenta.warpMenuConfig.getConfig()
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
                    .toMutableList()
                itemHomeBuilder.addLore(lores)
            }

            val item = ItemBuilder.from(itemHomeBuilder.setGlowing(true).create()).asGuiItem { action ->
                if (action.isLeftClick || action.isRightClick) {
                    player.teleport(magenta.warpModel.toLocation(warp.warpName))
                    return@asGuiItem
                }
            }
            gui.addItem(item)
        }

        paginationMenu.paginatedControlButtons(player, magenta.warpMenuConfig.getConfig(), gui)
        actionCustomButtons(player, magenta.warpMenuConfig.getConfig(), gui)

        gui.open(player)
    }

    private fun actionCustomButtons(player: HumanEntity, config: FileConfiguration, gui: PaginatedGui) {

        for (el in config.getConfigurationSection("menu.items.buttons")?.getKeys(false)!!) {
            val material = Material.getMaterial(config.getString("menu.items.buttons.${el}.icon").toString()) ?: continue
            if (config.contains("menu.items.buttons.$el")) {
                if (!config.contains("menu.items.buttons.$el.name"))
                    return player.sendMessage(magenta.locale.translation("magenta.menu.error.not.defined.name",
                        Placeholder.parsed("category", config.name)
                    ))

                val itemStack = com.github.encryptsl.magenta.api.ItemBuilder(material, 1)

                itemStack.setName(ModernText.miniModernText(config.getString("menu.items.buttons.${el}.name").toString()))

                val lores = config
                    .getStringList("menu.items.buttons.$el.lore")
                    .map { ModernText.miniModernText(it) }
                    .toMutableList()

                itemStack.addLore(lores)

                val actionItems = ItemBuilder.from(itemStack.create()).asGuiItem { action ->
                    if (action.isLeftClick) {
                        paginationMenu.clickSound(action.whoClicked, config)
                        openOwnerWarps(player, config, el)
                    }
                }
                gui.setItem(config.getInt("menu.items.buttons.$el.positions.row"), config.getInt("menu.items.buttons.$el.positions.col"), actionItems)
            }
        }
    }

    private fun openOwnerWarps(player: HumanEntity, fileConfiguration: FileConfiguration, el: String) {
        if (fileConfiguration.getString("menu.items.buttons.$el.action").equals("OPEN_MENU", true)) {
            warpPlayerGUI.openMenu(player)
        }
    }

}