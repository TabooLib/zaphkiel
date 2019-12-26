package ink.ptms.zaphkiel.api.internal

import com.google.common.collect.Lists
import com.google.common.collect.Maps
import ink.ptms.zaphkiel.ZaphkielAPI
import ink.ptms.zaphkiel.api.Item
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.module.lite.SimpleIterator
import io.izzel.taboolib.util.item.ItemBuilder
import io.izzel.taboolib.util.item.Items
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.util.function.Consumer

/**
 * @Author 坏黑
 * @Since 2019-01-03 20:25
 */
@TListener(cancel = "cancel")
class ItemList : Listener {

    fun cancel() {
        Bukkit.getOnlinePlayers().stream().filter { player -> player.openInventory.topInventory.holder is ItemListHolder }.forEach { it.closeInventory() }
    }

    @EventHandler
    fun onClick(e: InventoryClickEvent) {
        if (e.inventory.holder is ItemListHolder) {
            e.isCancelled = true
            // 空物品
            if (Items.isNull(e.currentItem)) {
                return
            }
            // 点击物品
            if ((e.inventory.holder as ItemListHolder).items.containsKey(e.rawSlot)) {
                val item = ZaphkielAPI.registeredItem[(e.inventory.holder as ItemListHolder).items[e.rawSlot]]
                if (item != null) {
                    val itemStack = item.build(e.whoClicked as Player).save()
                    itemStack.amount = if (e.click.isShiftClick) itemStack.type.maxStackSize else 1
                    e.whoClicked.inventory.addItem(itemStack)
                }
            }
            // 上一页
            if (e.rawSlot == 47) {
                open(e.whoClicked as Player, (e.inventory.holder as ItemListHolder).page - 1)
            }
            // 下一页
            if (e.rawSlot == 51) {
                open(e.whoClicked as Player, (e.inventory.holder as ItemListHolder).page + 1)
            }
        }
    }

    class ItemListHolder(val page: Int) : InventoryHolder {
        val items = Maps.newHashMap<Int, String>()!!

        override fun getInventory(): Inventory {
            return Bukkit.createInventory(null, 0)
        }
    }

    internal companion object {

        fun open(player: Player, page: Int) {
            val itemsAll = ZaphkielAPI.registeredItem.keys.toList()
            val holder = ItemListHolder(page)
            val maxPage = Math.floor(itemsAll.size / 28.0).toInt() + 1
            val inventory = Bukkit.createInventory(holder, 54, "Zaphkiel ItemList : " + (page + 1) + "/" + maxPage)
            val items = SimpleIterator(itemsAll).listIterator(page * 28, (page + 1) * 28)
            for (i in items.indices) {
                try {
                    val item = ZaphkielAPI.registeredItem[items[i]]!!.build(player).save()
                    val itemMeta = item.itemMeta
                    val lore = if (itemMeta!!.hasLore()) itemMeta.lore else Lists.newArrayList()
                    lore!!.add("")
                    lore.add("§7序号: §8" + items[i])
                    itemMeta.lore = lore
                    item.itemMeta = itemMeta
                    inventory.setItem(Items.INVENTORY_CENTER[i], item)
                } catch (t: Throwable) {
                    inventory.setItem(Items.INVENTORY_CENTER[i], ItemBuilder(Material.BARRIER).name("§4Invalid Item: " + items[i]).build())
                }
                holder.items[Items.INVENTORY_CENTER[i]] = items[i].toString()
            }
            if (page > 0) {
                inventory.setItem(47, ItemBuilder(Material.ARROW).name("§f上一页").build())
            }
            if (page < maxPage - 1) {
                inventory.setItem(51, ItemBuilder(Material.ARROW).name("§f下一页").build())
            }
            player.openInventory(inventory)
        }
    }
}
