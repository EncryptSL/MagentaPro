package com.github.encryptsl.magenta.api.menu.modules.warp

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.kmono.lib.utils.ItemBuilder
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.MenuUI
import com.github.encryptsl.magenta.api.menu.components.template.Menu
import dev.triumphteam.gui.container.GuiContainer
import dev.triumphteam.gui.item.GuiItem
import dev.triumphteam.gui.paper.Gui
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
        val gui = Gui.of(rows).title(
            ModernText.miniModernText(magenta.warpMenuConfig.getConfig().getString("menu.gui.display").toString(),
                Placeholder.parsed("count", magenta.warpModel.getWarps().join().count().toString()))
        )


        gui.component { component ->
            component.render { container, viewer ->
                actionCustomButtons(viewer, container, magenta.warpMenuConfig.getConfig())
            }
            component.render { container, _ ->
                menuUI.useAllFillers(rows, container, magenta.warpMenuConfig.getConfig())
                val warps = magenta.warpModel.getWarps().join()
                for (warp in warps.withIndex()) {
                    val material = Material.getMaterial(warp.value.warpIcon) ?: Material.OAK_SIGN

                    if (!magenta.warpMenuConfig.getConfig().contains("menu.warp-info.display")) continue
                    if (!magenta.warpMenuConfig.getConfig().contains("menu.warp-info.lore")) continue

                    val itemComponentName = ModernText.miniModernText(magenta.warpMenuConfig.getConfig().getString("menu.warp-info.display").toString(),
                        Placeholder.parsed("warp", warp.value.warpName)
                    )

                    val lore = magenta.warpMenuConfig.getConfig()
                        .getStringList("menu.warp-info.lore")
                        .map { ModernText.miniModernText(it, TagResolver.resolver(
                            Placeholder.parsed("owner", warp.value.owner),
                            Placeholder.parsed("warp", warp.value.warpName),
                            Placeholder.parsed("world", warp.value.world),
                            Placeholder.parsed("x", warp.value.x.toString()),
                            Placeholder.parsed("y", warp.value.y.toString()),
                            Placeholder.parsed("z", warp.value.z.toString()),
                            Placeholder.parsed("yaw", warp.value.yaw.toString()),
                            Placeholder.parsed("pitch", warp.value.pitch.toString()),
                        )) }

                    val itemBuilder = ItemBuilder(material, 1).setName(itemComponentName).addLore(lore.toMutableList()).create()

                    val item = dev.triumphteam.gui.paper.builder.item.ItemBuilder.from(itemBuilder).asGuiItem { p, context ->
                        player.teleport(magenta.warpModel.toLocation(warp.value.warpName))
                        return@asGuiItem
                    }
                    container.set(warp.index, item)
                }
            }
        }.build().open(player)
    }


    private fun actionCustomButtons(
        player: Player, container: GuiContainer<Player, ItemStack>, config: FileConfiguration
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

                container.set(config.getInt("menu.items.buttons.$el.positions.row"), config.getInt("menu.items.buttons.$el.positions.col"), actionItem)
            }
        }
    }

    private fun getItem(
        itemStack: ItemStack,
        config: FileConfiguration,
        el: String
    ): GuiItem<Player, ItemStack> {
        return dev.triumphteam.gui.paper.builder.item.ItemBuilder.from(itemStack).asGuiItem { p, context ->
            openOwnerWarps(p, config, el)
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