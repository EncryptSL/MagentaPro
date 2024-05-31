package com.github.encryptsl.magenta.api.menu.modules.warp

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.kmono.lib.extensions.createItem
import com.github.encryptsl.kmono.lib.extensions.meta
import com.github.encryptsl.kmono.lib.extensions.setLoreComponentList
import com.github.encryptsl.kmono.lib.extensions.setNameComponent
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.MenuUI
import dev.triumphteam.gui.container.GuiContainer
import dev.triumphteam.gui.layout.BoxGuiLayout
import dev.triumphteam.gui.paper.Gui
import dev.triumphteam.gui.paper.builder.item.ItemBuilder
import dev.triumphteam.gui.slot.Slot
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class WarpPlayerEditorGUI(private val magenta: Magenta) {

    enum class BUTTON_ACTION { BACK_TO_MENU, SET_WARP, SET_ICON, DELETE_HOME }

    private val menu: MenuUI by lazy { MenuUI(magenta) }
    private val warpPlayerGUI: WarpPlayerGUI by lazy { WarpPlayerGUI(magenta, WarpGUI(magenta), this) }

    private val ignoreSlots = listOf(17, 18, 26, 27, 35, 36, 44)

    private var clicked = false

    fun openWarpPlayerEditor(player: Player, warpName: String) {
        val rows = magenta.warpEditorConfig.getConfig().getInt("menu.gui.size", 6)

        val gui = Gui.of(rows).title(
            ModernText.miniModernText(
                magenta.warpEditorConfig.getConfig().getString("menu.gui.display").toString(),
                Placeholder.parsed("warp", warpName)
            )
        )

        val buttons = magenta.warpEditorConfig.getConfig().getConfigurationSection("menu.items.buttons") ?: return

        gui.component { component ->
            component.render { container, viewer ->
                menu.useAllFillers(rows, container, magenta.warpEditorConfig.getConfig())

                for (el in buttons.getKeys(false).withIndex()) {
                    val material = Material.getMaterial(magenta.warpEditorConfig.getConfig().getString("menu.items.buttons.${el.value}.icon").toString()) ?: continue
                    if (magenta.warpEditorConfig.getConfig().contains("menu.items.buttons.${el.value}")) {
                        if (!magenta.warpEditorConfig.getConfig().contains("menu.items.buttons.${el.value}.name"))
                            return@render viewer.sendMessage(magenta.locale.translation("magenta.menu.error.not.defined.name",
                                Placeholder.parsed("category", magenta.warpEditorConfig.getConfig().name)
                            ))

                        if (!magenta.warpEditorConfig.getConfig().contains("menu.items.buttons.${el.value}.slot"))
                            return@render viewer.sendMessage(magenta.locale.translation("magenta.menu.error.not.defined.slot",
                                Placeholder.parsed("category", magenta.warpEditorConfig.getConfig().name)
                            ))

                        if (!magenta.warpEditorConfig.getConfig().contains("menu.items.buttons.${el.value}.icon"))
                            return@render viewer.sendMessage(magenta.locale.translation("magenta.menu.error.not.defined.icon",
                                Placeholder.parsed("category", magenta.warpEditorConfig.getConfig().name)
                            ))

                        val lore = magenta.warpEditorConfig
                            .getConfig()
                            .getStringList("menu.items.buttons.$el.lore")
                            .map { ModernText.miniModernText(it) }
                            .toMutableList()

                        val itemStack = createItem(material) {
                            amount = 1
                            meta {
                                setNameComponent = ModernText.miniModernText(magenta.warpEditorConfig.getConfig().getString("menu.items.buttons.${el.value}.name").toString())
                                setLoreComponentList = lore
                            }
                        }

                        val actionItems = ItemBuilder.from(itemStack).asGuiItem { whoClick, context ->
                            //if (action.isLeftClick) {
                                //editorActionButton(whoClick, warpName, magenta.warpEditorConfig.getConfig(), el.value, container)
                            //}
                        }
                        container.set(el.index, actionItems)
                    }
                }
            }
        }.build().open(player)
    }

    private fun editorActionButton(
        humanEntity: Player,
        warpName: String,
        config: FileConfiguration,
        el: String,
        container: GuiContainer<Player, ItemStack>
    ) {
        val action = BUTTON_ACTION.valueOf(config.getString("menu.items.buttons.$el.action").toString())

        when(action) {
            BUTTON_ACTION.BACK_TO_MENU -> {
                clicked = false
                warpPlayerGUI.open(humanEntity)
            }
            BUTTON_ACTION.SET_WARP -> {
                clicked = false
                //clearIcons(gui)
                magenta.warpModel.moveWarp(humanEntity.uniqueId, warpName, humanEntity.location)
                humanEntity.sendMessage(magenta.locale.translation("magenta.command.warp.success.moved", TagResolver.resolver(
                    Placeholder.parsed("warp", warpName),
                    Placeholder.parsed("x", humanEntity.location.x.toInt().toString()),
                    Placeholder.parsed("y", humanEntity.location.y.toInt().toString()),
                    Placeholder.parsed("z", humanEntity.location.z.toInt().toString())
                )))
            }
            BUTTON_ACTION.SET_ICON -> {
                loadIcons(humanEntity, container, warpName, config)
                //gui.update()
            }
            BUTTON_ACTION.DELETE_HOME -> {
                clicked = false
                magenta.homeModel.deleteHome(humanEntity.uniqueId, warpName)
                //gui.close(humanEntity)
                humanEntity.sendMessage(magenta.locale.translation("magenta.command.warp.success.deleted", Placeholder.parsed("home", warpName)))
            }
        }
    }

    private fun loadIcons(
        player: Player,
        container: GuiContainer<Player, ItemStack>,
        warpName: String,
        fileConfiguration: FileConfiguration
    ) {
        val icons: Set<String> = fileConfiguration.getStringList("menu.icons").filter { s -> Material.getMaterial(s) != null }.map { it }.toSet()
        val itemName = fileConfiguration.getString("menu.icon.name")

        if (clicked) return
        clicked = true
        for (icon in icons) {
            val lore = fileConfiguration.getStringList("menu.icon.lore")
                .map { ModernText.miniModernText(it, Placeholder.parsed("icon", icon)) }
                .toMutableList()
            setIcon(player, warpName, itemName ?: icon, container, icon, lore)
        }
    }

    private fun setIcon(
        player: Player,
        warpName: String,
        itemName: String,
        container: GuiContainer<Player, ItemStack>,
        materialName: String,
        lore: MutableList<Component>
    ) {
        val material = Material.getMaterial(materialName)!!
        for (slot in BoxGuiLayout(Slot.of(3, 1), Slot.of(4, 9)).generatePositions()) {
            container.set(slot,ItemBuilder.from(
                com.github.encryptsl.kmono.lib.utils.ItemBuilder(material, 1)
                    .setName(
                        ModernText.miniModernText(itemName,
                            Placeholder.parsed("icon", materialName))
                    ).addLore(lore)
                    .create()
            ).asGuiItem { whoClick, context ->
                magenta.warpModel.setWarpIcon(whoClick.uniqueId, warpName, materialName)
                //if (action.isLeftClick) {
                //    player.sendMessage(magenta.locale.translation("magenta.command.warp.success.change.icon", TagResolver.resolver(
                //        Placeholder.parsed("warp", warpName),
                //        Placeholder.parsed("icon", materialName)
                //    )))
                //}
            })
        }
    }

    //private fun clearIcons(gui: Gui) {
    //    for (i in 10 .. 43) {
    //        if (!ignoreSlots.contains(i)) {
    //            gui.removeItem(i)
    //        }
    //    }
    //}
}