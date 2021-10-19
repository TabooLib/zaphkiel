package ink.ptms.zaphkiel.api.event

import ink.ptms.zaphkiel.ZaphkielAPI
import ink.ptms.zaphkiel.api.ItemStream
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.EquipmentSlot
import taboolib.common.platform.Schedule
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.util.isNotAir

/**
 * @author sky
 * @since 2020-04-20 12:37
 */
internal object ItemListener {

    @Schedule(period = 100, async = true)
    fun e() {
        Bukkit.getOnlinePlayers().forEach {
            it.inventory.filter { item -> item.isNotAir() }.forEach { item ->
                val event = ItemEvent.AsyncTick(ZaphkielAPI.read(item), it)
                event.call()
                if (event.save) {
                    event.itemStream.rebuildToItemStack(it)
                }
            }
        }
    }

    fun Player.select() {
        inventory.filter { it.isNotAir() }.forEach {
            val event = ItemEvent.Select(ZaphkielAPI.read(it), this)
            event.call()
            if (event.save) {
                event.itemStream.rebuildToItemStack(this@select)
            }
        }
    }

    @SubscribeEvent
    fun e(e: PlayerJoinEvent) {
        e.player.select()
    }

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: PlayerChangedWorldEvent) {
        e.player.select()
    }

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: PlayerItemConsumeEvent) {
        if (e.item.isNotAir()) {
            val event = ItemEvent.Consume(ZaphkielAPI.read(e.item), e)
            event.call()
            if (event.save) {
                event.itemStream.rebuildToItemStack(e.player)
            }
        }
    }

    @SubscribeEvent
    fun e(e: PlayerInteractEvent) {
        if (e.item.isNotAir()) {
            val event = ItemEvent.Interact(ZaphkielAPI.read(e.item!!), e)
            event.call()
            if (event.save) {
                event.itemStream.rebuildToItemStack(e.player)
            }
        }
    }

    @SubscribeEvent
    fun e(e: PlayerInteractEntityEvent) {
        if (e.player.inventory.itemInMainHand.isNotAir() && e.hand == EquipmentSlot.HAND) {
            val event = ItemEvent.InteractEntity(ZaphkielAPI.read(e.player.inventory.itemInMainHand), e)
            event.call()
            if (event.save) {
                event.itemStream.rebuildToItemStack(e.player)
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: PlayerDropItemEvent) {
        if (e.itemDrop.itemStack.isNotAir()) {
            val event = ItemEvent.Drop(ZaphkielAPI.read(e.itemDrop.itemStack), e)
            event.call()
            if (event.save) {
                e.itemDrop.itemStack = event.itemStream.rebuildToItemStack(e.player)
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: EntityPickupItemEvent) {
        if (e.item.itemStack.isNotAir() && e.entity is Player) {
            val event = ItemEvent.Pick(ZaphkielAPI.read(e.item.itemStack), e)
            event.call()
            if (event.save) {
                e.item.itemStack = event.itemStream.rebuildToItemStack(e.entity as Player)
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: InventoryClickEvent) {
        val itemStreamCurrent = if (e.currentItem.isNotAir()) ZaphkielAPI.read(e.currentItem!!) else null
        var itemStreamButton: ItemStream? = null
        if (e.click == ClickType.NUMBER_KEY) {
            val hotbarButton = e.whoClicked.inventory.getItem(e.hotbarButton)
            if (hotbarButton.isNotAir()) {
                itemStreamButton = ZaphkielAPI.read(hotbarButton!!)
            }
        }
        if (itemStreamCurrent == null && itemStreamButton == null) {
            return
        }
        val event = ItemEvent.InventoryClick(itemStreamCurrent, itemStreamButton, e)
        event.call()
        if (event.saveCurrent && itemStreamCurrent != null) {
            itemStreamCurrent.rebuildToItemStack(e.whoClicked as Player)
        }
        if (event.saveButton && itemStreamButton != null) {
            itemStreamButton.rebuildToItemStack(e.whoClicked as Player)
        }
    }
}