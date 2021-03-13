package ink.ptms.zaphkiel.api.event.item

import ink.ptms.zaphkiel.ZaphkielAPI
import ink.ptms.zaphkiel.api.ItemStream
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.module.inject.TSchedule
import io.izzel.taboolib.util.item.Items
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.EquipmentSlot

/**
 * @Author sky
 * @Since 2020-04-20 12:37
 */
@TListener
class ItemListener : Listener {

    @TSchedule(period = 100, async = true)
    fun e() {
        Bukkit.getOnlinePlayers().forEach {
            it.inventory.filter { item -> Items.nonNull(item) }.forEach { item ->
                ItemEvents.AsyncTick(ZaphkielAPI.read(item), it).call().run {
                    if (save) {
                        itemStream.rebuild(it)
                    }
                }
            }
        }
    }

    @EventHandler
    fun e(e: PlayerJoinEvent) {
        e.player.inventory.filter { Items.nonNull(it) }.forEach {
            ItemEvents.Select(ZaphkielAPI.read(it), e.player).call().run {
                if (save) {
                    itemStream.rebuild(e.player)
                }
            }
        }
    }

    @EventHandler
    fun e(e: PlayerItemConsumeEvent) {
        if (Items.nonNull(e.item)) {
            ItemEvents.Consume(ZaphkielAPI.read(e.item), e).call().run {
                if (save) {
                    itemStream.rebuild(e.player)
                }
            }
        }
    }

    @EventHandler
    fun e(e: PlayerInteractEvent) {
        if (Items.nonNull(e.item)) {
            ItemEvents.Interact(ZaphkielAPI.read(e.item!!), e).call().run {
                if (save) {
                    itemStream.rebuild(e.player)
                }
            }
        }
    }

    @EventHandler
    fun e(e: PlayerInteractEntityEvent) {
        if (Items.nonNull(e.player.inventory.itemInMainHand) && e.hand == EquipmentSlot.HAND) {
            ItemEvents.InteractEntity(ZaphkielAPI.read(e.player.inventory.itemInMainHand), e).call().run {
                if (save) {
                    itemStream.rebuild(e.player)
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: PlayerDropItemEvent) {
        if (Items.nonNull(e.itemDrop.itemStack)) {
            ItemEvents.Drop(ZaphkielAPI.read(e.itemDrop.itemStack), e).call().run {
                if (save) {
                    e.itemDrop.setItemStack(itemStream.rebuild(e.player))
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: EntityPickupItemEvent) {
        if (Items.nonNull(e.item.itemStack) && e.entity is Player) {
            ItemEvents.Pick(ZaphkielAPI.read(e.item.itemStack), e).call().run {
                if (save) {
                    e.item.setItemStack(itemStream.rebuild(e.entity as Player))
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: InventoryClickEvent) {
        val itemStreamCurrent = if (Items.nonNull(e.currentItem)) ZaphkielAPI.read(e.currentItem!!) else null
        var itemStreamButton: ItemStream? = null
        if (e.click == ClickType.NUMBER_KEY) {
            val hotbarButton = e.whoClicked.inventory.getItem(e.hotbarButton)
            if (Items.nonNull(hotbarButton)) {
                itemStreamButton = ZaphkielAPI.read(hotbarButton!!)
            }
        }
        if (itemStreamCurrent == null && itemStreamButton == null) {
            return
        }
        ItemEvents.InventoryClick(itemStreamCurrent, itemStreamButton, e).call().run {
            if (saveCurrent && itemStreamCurrent != null) {
                itemStreamCurrent.rebuild(e.whoClicked as Player)
            }
            if (saveButton && itemStreamButton != null) {
                itemStreamButton.rebuild(e.whoClicked as Player)
            }
        }
    }
}