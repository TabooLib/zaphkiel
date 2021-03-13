package ink.ptms.zaphkiel.api.internal

import ink.ptms.zaphkiel.ZaphkielAPI
import ink.ptms.zaphkiel.api.Group
import ink.ptms.zaphkiel.api.Item
import io.izzel.taboolib.cronus.CronusUtils
import io.izzel.taboolib.kotlin.Indexed
import io.izzel.taboolib.util.item.ItemBuilder
import io.izzel.taboolib.util.item.Items
import io.izzel.taboolib.util.item.inventory.ClickType
import io.izzel.taboolib.util.item.inventory.MenuBuilder
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag

/**
 * @Author 坏黑
 * @Since 2019-01-03 20:25
 */
object ItemList {

    fun open(player: Player, page: Int) {
        val objectsMap = HashMap<Int, Group>()
        val itemsAll = ZaphkielAPI.registeredItem.values.groupBy { it.group ?: Group.NO_GROUP }.toList().sortedByDescending { it.first.priority }
        val items = Indexed.subList(itemsAll, page * 28, (page + 1) * 28 - 1)
        MenuBuilder.builder()
                .title("Zaphkiel Items - ${page + 1}")
                .rows(6)
                .event { e ->
                    e.isCancelled = true
                    if (e.clickType == ClickType.CLICK) {
                         if (e.rawSlot == 47 && e.currentItem!!.type == Material.SPECTRAL_ARROW) {
                            open(player, page - 1)
                        } else if (e.rawSlot == 51 && e.currentItem!!.type == Material.SPECTRAL_ARROW) {
                            open(player, page + 1)
                        } else if (objectsMap.containsKey(e.rawSlot)) {
                            open(player, objectsMap[e.rawSlot]!!, 0)
                        }
                    }
                }
                .build { inventory ->
                    items.forEachIndexed { index, item ->
                        objectsMap[Items.INVENTORY_CENTER[index]] = item.first
                        inventory.setItem(Items.INVENTORY_CENTER[index], ItemBuilder(item.first.display)
                                .flags(ItemFlag.HIDE_ATTRIBUTES)
                                .build())
                    }
                    if (page > 0) {
                        inventory.setItem(47, ItemBuilder(Material.SPECTRAL_ARROW).name("§e上一页").build())
                    } else {
                        inventory.setItem(47, ItemBuilder(Material.ARROW).name("§8上一页").build())
                    }
                    if (CronusUtils.next(page, itemsAll.size, 28)) {
                        inventory.setItem(51, ItemBuilder(Material.SPECTRAL_ARROW).name("§e下一页").build())
                    } else {
                        inventory.setItem(51, ItemBuilder(Material.ARROW).name("§8下一页").build())
                    }
                }.open(player)
        player.playSound(player.location, Sound.UI_BUTTON_CLICK, 1f, 2f)
    }

    fun open(player: Player, group: Group, page: Int) {
        val objectsMap = HashMap<Int, Item>()
        val itemsAll = ZaphkielAPI.registeredItem.values.filter { (it.group ?: Group.NO_GROUP) == group }.sortedBy { it.icon.type.name }
        val items = Indexed.subList(itemsAll, page * 28, (page + 1) * 28 - 1)
        MenuBuilder.builder()
                .title("Zaphkiel Items - ${page + 1} (${group.name})")
                .rows(6)
                .event { e ->
                    e.isCancelled = true
                    if (e.clickType == ClickType.CLICK) {
                        if (e.rawSlot == 49) {
                            open(player, 0)
                        } else if (e.rawSlot == 47 && e.currentItem!!.type == Material.SPECTRAL_ARROW) {
                            open(player, group, page - 1)
                        } else if (e.rawSlot == 51 && e.currentItem!!.type == Material.SPECTRAL_ARROW) {
                            open(player, group, page + 1)
                        } else if (objectsMap.containsKey(e.rawSlot)) {
                            e.clicker.inventory.addItem(objectsMap[e.rawSlot]!!.build(e.clicker).save().also {
                                it.amount = if (e.castClick().isShiftClick) it.type.maxStackSize else 1
                            })
                        }
                    }
                }
                .build { inventory ->
                    items.forEachIndexed { index, item ->
                        objectsMap[Items.INVENTORY_CENTER[index]] = item
                        inventory.setItem(Items.INVENTORY_CENTER[index], item.build(player).save().also {
                            val itemMeta = it.itemMeta!!
                            itemMeta.lore = itemMeta.lore!!.also { lore ->
                                lore.add("")
                                lore.add("§7序号: §f${item.id}")
                            }
                        })
                    }
                    if (page > 0) {
                        inventory.setItem(47, ItemBuilder(Material.SPECTRAL_ARROW).name("§e上一页").build())
                    } else {
                        inventory.setItem(47, ItemBuilder(Material.ARROW).name("§8上一页").build())
                    }
                    if (CronusUtils.next(page, itemsAll.size, 28)) {
                        inventory.setItem(51, ItemBuilder(Material.SPECTRAL_ARROW).name("§e下一页").build())
                    } else {
                        inventory.setItem(51, ItemBuilder(Material.ARROW).name("§8下一页").build())
                    }
                    inventory.setItem(49, ItemBuilder(Material.REDSTONE).name("§c返回").build())
                }.open(player)
        player.playSound(player.location, Sound.UI_BUTTON_CLICK, 1f, 2f)
    }
}
