@file:Suppress("DuplicatedCode")

package ink.ptms.zaphkiel.impl.feature

import ink.ptms.zaphkiel.Zaphkiel
import ink.ptms.zaphkiel.api.Group
import ink.ptms.zaphkiel.api.Item
import ink.ptms.zaphkiel.impl.internal.ItemListener.onClick
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import taboolib.library.xseries.XMaterial
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import taboolib.module.ui.type.PageableChest
import taboolib.platform.util.Slots
import taboolib.platform.util.buildItem
import taboolib.platform.util.modifyLore
import taboolib.platform.util.modifyMeta

/**
 * 打开组菜单
 */
fun Player.openGroupMenu(parent: Group? = null) {
    // 播放音效
    playSound(location, Sound.UI_BUTTON_CLICK, 1f, 2f)
    // 打开页面
    openMenu<PageableChest<MenuItem>>("Zaphkiel Items (Page %p)") {
        rows(6)
        slots(Slots.CENTER)
        elements {
            val items = arrayListOf<MenuItem>()
            // 获取当前层级下的所有组
            items += Zaphkiel.api().getItemManager().getGroupMap().values.filter { it.parent == parent }.map { MenuItem.of(it) }
            // 如果存在父组，则获取组内所有物品
            if (parent != null) {
                items += parent.getItems().map { MenuItem.of(it) }
            }
            items
        }
        // 生成物品
        onGenerate { _, element, _, _ -> element.icon() }
        // 点击
        onClick { event, element -> element.click(event.clicker) }
        // 翻页
        setNextPage(51) { _, hasNextPage ->
            if (hasNextPage) {
                buildItem(XMaterial.SPECTRAL_ARROW) { name = "§7Next" }
            } else {
                buildItem(XMaterial.ARROW) { name = "§8Next" }
            }
        }
        setPreviousPage(47) { _, hasPreviousPage ->
            if (hasPreviousPage) {
                buildItem(XMaterial.SPECTRAL_ARROW) { name = "§7Previous" }
            } else {
                buildItem(XMaterial.ARROW) { name = "§8Previous" }
            }
        }
    }
}

interface MenuItem {

    fun icon(): ItemStack

    fun click(player: Player)

    companion object {

        fun of(item: Item) = object : MenuItem {

            override fun icon(): ItemStack {
                return item.buildItemStack().modifyMeta<ItemMeta> {
                    // 修改描述
                    modifyLore {
                        add("")
                        add("§7ID: ${item.id}")
                    }
                    // 隐藏标签
                    addItemFlags(*ItemFlag.values())
                }
            }

            override fun click(player: Player) {
                item.giveItemOrDrop(player)
            }
        }

        fun of(group: Group) = object : MenuItem {

            override fun icon(): ItemStack {
                return group.display
            }

            override fun click(player: Player) {
                player.openGroupMenu(group)
            }
        }
    }
}