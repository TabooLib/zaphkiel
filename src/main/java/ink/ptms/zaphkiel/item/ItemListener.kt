package ink.ptms.zaphkiel.item

import ink.ptms.zaphkiel.ZaphkielAPI
import ink.ptms.zaphkiel.api.ItemStream
import ink.ptms.zaphkiel.api.event.ItemBuildEvent
import ink.ptms.zaphkiel.api.event.ItemEvent
import ink.ptms.zaphkiel.api.event.ItemReleaseEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.EquipmentSlot
import taboolib.common.platform.Schedule
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.util.attacker
import taboolib.platform.util.isAir
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

    @SubscribeEvent
    fun e(e: ItemBuildEvent.Pre) {
        e.itemStream.getZaphkielItem().invokeScript("onBuild", e.player, e, e.itemStream, "zaphkiel-build")
    }

    @SubscribeEvent
    fun e(e: ItemReleaseEvent) {
        e.itemStream.getZaphkielItem().invokeScript("onRelease", e.player, e, e.itemStream, "zaphkiel-build")
    }

    @SubscribeEvent
    fun e(e: ItemReleaseEvent.Display) {
        e.itemStream.getZaphkielItem().invokeScript("onReleaseDisplay", e.player, e, e.itemStream, "zaphkiel-build")
    }

    @SubscribeEvent
    fun e(e: EntityDamageByEntityEvent) {
        val attacker = e.attacker
        if (attacker is Player && attacker.itemInHand.isNotAir()) {
            val itemStream = ZaphkielAPI.read(attacker.itemInHand)
            if (itemStream.isExtension()) {
                itemStream.getZaphkielItem().invokeScript("onAttack", attacker, e, itemStream)
            }
        }
    }

    @SubscribeEvent
    fun e(e: PlayerJoinEvent) {
        e.player.onSelect()
    }

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: PlayerChangedWorldEvent) {
        e.player.onSelect()
    }

    /**
     * 当玩家物品发生损坏时
     * 触发事件
     */
    @SubscribeEvent(ignoreCancelled = true)
    fun e(e: PlayerItemBreakEvent) {
        val itemStream = ZaphkielAPI.read(e.brokenItem)
        if (itemStream.isExtension()) {
            itemStream.getZaphkielItem().invokeScript("onItemBreak", e, itemStream)
        }
    }

    /**
     * 当玩家消耗物品时
     * 触发事件及脚本
     */
    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: PlayerItemConsumeEvent) {
        val itemStack = e.item
        if (itemStack.isAir()) {
            return
        }
        val itemStream = ZaphkielAPI.read(itemStack)
        if (itemStream.isExtension()) {
            // 触发事件
            ItemEvent.Consume(itemStream, e).also { it.call() }
            // 执行脚本
            itemStream.getZaphkielItem().invokeScript("onConsume", e, itemStream)
            // 更新物品
            if (e.item == e.player.inventory.itemInMainHand) {
                e.player.inventory.setItemInMainHand(itemStack)
            } else {
                e.player.inventory.setItemInOffHand(itemStack)
            }
        }
    }

    /**
     * 当玩家与空气或方块发生交互时
     * 触发事件及脚本
     */
    @SubscribeEvent
    fun e(e: PlayerInteractEvent) {
        if (e.item.isAir()) {
            return
        }
        val itemStream = ZaphkielAPI.read(e.item!!)
        if (itemStream.isVanilla()) {
            return
        }
        // 触发事件
        val event = ItemEvent.Interact(itemStream, e)
        if (event.call()) {
            if (event.save) {
                event.itemStream.rebuildToItemStack(e.player)
            }
            // 执行脚本
            when (e.action) {
                Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK -> {
                    itemStream.getZaphkielItem().invokeScript("onLeftClick", e, itemStream)
                }
                Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK -> {
                    itemStream.getZaphkielItem().invokeScript("onRightClick", e, itemStream)
                }
                else -> {
                }
            }
        }
    }

    /**
     * 当玩家与实体发生交互时
     * 触发事件及脚本
     */
    @SubscribeEvent
    fun e(e: PlayerInteractEntityEvent) {
        if (e.player.inventory.itemInMainHand.isNotAir() && e.hand == EquipmentSlot.HAND) {
            val itemStream = ZaphkielAPI.read(e.player.inventory.itemInMainHand)
            if (itemStream.isVanilla()) {
                return
            }
            val event = ItemEvent.InteractEntity(itemStream, e)
            if (event.call()) {
                if (event.save) {
                    event.itemStream.rebuildToItemStack(e.player)
                }
                itemStream.getZaphkielItem().invokeScript("onRightClickEntity", e, itemStream)
            }
        }
    }

    /**
     * 当玩家切换副手时
     * 触发脚本
     */
    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: PlayerSwapHandItemsEvent) {
        if (e.offHandItem.isNotAir()) {
            val itemStream = ZaphkielAPI.read(e.offHandItem!!)
            if (itemStream.isExtension()) {
                itemStream.getZaphkielItem().invokeScript("onSwapToOffhand", e, itemStream)
            }
        }
        if (e.mainHandItem.isNotAir()) {
            val itemStream = ZaphkielAPI.read(e.mainHandItem!!)
            if (itemStream.isExtension()) {
                itemStream.getZaphkielItem().invokeScript("onSwapToMainHand", e, itemStream)
            }
        }
    }

    /**
     * 当玩家破坏方块时
     * 触发脚本
     */
    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: BlockBreakEvent) {
        if (e.player.inventory.itemInMainHand.isAir()) {
            return
        }
        val itemStream = ZaphkielAPI.read(e.player.inventory.itemInMainHand)
        if (itemStream.isExtension()) {
            itemStream.getZaphkielItem().invokeScript("onBlockBreak", e.player, e, itemStream)
        }
    }

    /**
     * 当玩家丢弃物品时
     * 触发事件及脚本
     */
    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: PlayerDropItemEvent) {
        if (e.itemDrop.itemStack.isNotAir()) {
            val itemStream = ZaphkielAPI.read(e.itemDrop.itemStack)
            if (itemStream.isVanilla()) {
                return
            }
            val event = ItemEvent.Drop(itemStream, e)
            event.call()
            if (event.save) {
                e.itemDrop.setItemStack(event.itemStream.rebuildToItemStack(e.player))
            }
            // 若脚本修改物品则写回事件
            itemStream.getZaphkielItem().invokeScript("onDrop", e.player, e, itemStream)?.thenAccept {
                if (it != null) {
                    e.itemDrop.setItemStack(it.itemStack)
                }
            }
        }
    }

    /**
     * 当玩家捡起物品时
     * 触发事件及脚本
     */
    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: PlayerPickupItemEvent) {
        if (e.item.itemStack.isNotAir()) {
            val itemStream = ZaphkielAPI.read(e.item.itemStack)
            if (itemStream.isVanilla()) {
                return
            }
            val event = ItemEvent.Pick(itemStream, e)
            event.call()
            if (event.save) {
                e.item.setItemStack(event.itemStream.rebuildToItemStack(e.player))
            }
            // 若脚本修改物品则写回事件
            itemStream.getZaphkielItem().invokeScript("onPick", e.player, e, itemStream)?.thenAccept {
                if (it != null) {
                    e.item.setItemStack(it.itemStack)
                }
            }
        }
    }

    /**
     * 当玩家在背包中点击物品时
     * 触发事件
     */
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

    private fun Player.onSelect() {
        inventory.filter { it.isNotAir() }.forEach {
            val event = ItemEvent.Select(ZaphkielAPI.read(it), this)
            event.call()
            if (event.save) {
                event.itemStream.rebuildToItemStack(this@onSelect)
            }
        }
    }
}