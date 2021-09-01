package ink.ptms.zaphkiel.module

import ink.ptms.zaphkiel.ZaphkielAPI
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemBreakEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.util.isAir
import taboolib.platform.util.isNotAir

/**
 * @Author sky
 * @Since 2019-12-15 22:22
 */
internal object ItemEvents {

//    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true, bind = "ink.ptms.sandalphon.module.impl.blockmine.event.BlockBreakEvent")
//    fun e(oe: OptionalEvent) {
//        val e = oe.cast(ink.ptms.sandalphon.module.impl.blockmine.event.BlockBreakEvent::class.java)
//        if (e.player.inventory.itemInMainHand.isAir()) {
//            return
//        }
//        val itemStream = ZaphkielAPI.read(e.player.inventory.itemInMainHand)
//        if (itemStream.isExtension()) {
//            itemStream.getZaphkielItem().eval("onBlockBreak", e.player, e, e.player.inventory.itemInMainHand)
//        }
//    }

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: BlockBreakEvent) {
        if (e.player.inventory.itemInMainHand.isAir()) {
            return
        }
        val itemStream = ZaphkielAPI.read(e.player.inventory.itemInMainHand)
        if (itemStream.isExtension()) {
            itemStream.getZaphkielItem().eval("onBlockBreak", e.player, e, e.player.inventory.itemInMainHand)
        }
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun e(e: PlayerItemBreakEvent) {
        val itemStream = ZaphkielAPI.read(e.brokenItem)
        if (itemStream.isExtension()) {
            itemStream.getZaphkielItem().eval("onBreak", e, e.brokenItem)
        }
    }

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
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

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: PlayerSwapHandItemsEvent) {
        if (e.offHandItem.isNotAir()) {
            val itemStream = ZaphkielAPI.read(e.offHandItem!!)
            if (itemStream.isExtension()) {
                itemStream.getZaphkielItem().eval("onSwapToOffhand", e, e.offHandItem!!)
            }
        }
        if (e.mainHandItem.isNotAir()) {
            val itemStream = ZaphkielAPI.read(e.mainHandItem!!)
            if (itemStream.isExtension()) {
                itemStream.getZaphkielItem().eval("onSwapToMainHand", e, e.mainHandItem!!)
            }
        }
    }

    @SubscribeEvent
    fun e(e: PlayerInteractEvent) {
        if (e.item.isNotAir()) {
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