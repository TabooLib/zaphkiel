package ink.ptms.zaphkiel.module

import ink.ptms.zaphkiel.ZaphkielAPI
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.util.item.Items
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.*

/**
 * @Author sky
 * @Since 2019-12-15 22:22
 */
@TListener
private class ItemEvents : Listener {

    @TListener(depend = ["Sandalphon"])
    class SandalphonHook : Listener {

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        fun e(e: ink.ptms.sandalphon.module.impl.blockmine.event.BlockBreakEvent) {
            if (Items.isNull(e.player.inventory.itemInMainHand)) {
                return
            }
            val itemStream = ZaphkielAPI.read(e.player.inventory.itemInMainHand)
            if (itemStream.isExtension()) {
                itemStream.getZaphkielItem().eval("onBlockBreak", e.player, e, e.player.inventory.itemInMainHand)
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: BlockBreakEvent) {
        if (Items.isNull(e.player.inventory.itemInMainHand)) {
            return
        }
        val itemStream = ZaphkielAPI.read(e.player.inventory.itemInMainHand)
        if (itemStream.isExtension()) {
            itemStream.getZaphkielItem().eval("onBlockBreak", e.player, e, e.player.inventory.itemInMainHand)
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun e(e: PlayerItemBreakEvent) {
        val itemStream = ZaphkielAPI.read(e.brokenItem)
        if (itemStream.isExtension()) {
            itemStream.getZaphkielItem().eval("onBreak", e, e.brokenItem)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: PlayerItemConsumeEvent) {
        val itemStack = e.item
        val itemStream = ZaphkielAPI.read(itemStack)
        if (itemStream.isExtension()) {
            itemStream.getZaphkielItem().eval("onConsume", e, itemStack)
            if (e.item == e.player.inventory.itemInMainHand) {
                e.player.inventory.setItemInMainHand(itemStack)
            } else {
                e.player.inventory.setItemInOffHand(itemStack)
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: PlayerSwapHandItemsEvent) {
        if (Items.nonNull(e.offHandItem)) {
            val itemStream = ZaphkielAPI.read(e.offHandItem!!)
            if (itemStream.isExtension()) {
                itemStream.getZaphkielItem().eval("onSwapToOffhand", e, e.offHandItem!!)
            }
        }
        if (Items.nonNull(e.mainHandItem)) {
            val itemStream = ZaphkielAPI.read(e.mainHandItem!!)
            if (itemStream.isExtension()) {
                itemStream.getZaphkielItem().eval("onSwapToMainHand", e, e.mainHandItem!!)
            }
        }
    }

    @EventHandler
    fun e(e: PlayerInteractEvent) {
        if (Items.nonNull(e.item)) {
            val item = ZaphkielAPI.read(e.item!!)
            if (item.isVanilla()) {
                return
            }
            when (e.action) {
                Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK -> {
                    item.getZaphkielItem().eval("onLeftClick", e, e.item!!)
                }
                Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK -> {
                    item.getZaphkielItem().eval("onRightClick", e, e.item!!)
                }
                else -> {
                }
            }
        }
    }
}