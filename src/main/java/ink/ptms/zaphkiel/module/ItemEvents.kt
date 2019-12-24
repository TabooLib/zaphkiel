package ink.ptms.zaphkiel.module

import ink.ptms.zaphkiel.ZaphkielAPI
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.module.lite.SimpleReflection
import io.izzel.taboolib.util.item.Items
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemStack

/**
 * @Author sky
 * @Since 2019-12-15 22:22
 */
@TListener
class ItemEvents : Listener {

    @EventHandler(ignoreCancelled = true)
    fun e(e: PlayerItemBreakEvent) {
        val itemStream = ZaphkielAPI.read(e.brokenItem)
        if (itemStream.isExtension()) {
            itemStream.getZaphkielItem().eval("onBreak", e, e.brokenItem)
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun e(e: PlayerItemDamageEvent) {
        val itemStream = ZaphkielAPI.read(e.item)
        if (itemStream.isExtension()) {
            itemStream.getZaphkielItem().eval("onDamage", e, e.item)
        }
    }

    @EventHandler(ignoreCancelled = true)
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

    @EventHandler(ignoreCancelled = true)
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