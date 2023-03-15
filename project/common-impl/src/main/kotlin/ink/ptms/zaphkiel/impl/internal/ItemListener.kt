package ink.ptms.zaphkiel.impl.internal

import ink.ptms.zaphkiel.api.ItemStream
import ink.ptms.zaphkiel.api.event.ItemBuildEvent
import ink.ptms.zaphkiel.api.event.ItemEvent
import ink.ptms.zaphkiel.api.event.ItemReleaseEvent
import ink.ptms.zaphkiel.impl.internal.ItemListener.onSelect
import ink.ptms.zaphkiel.impl.item.toItemStream
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.Schedule
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.OptionalEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.console
import taboolib.module.nms.getItemTag
import taboolib.platform.util.attacker
import taboolib.platform.util.isAir
import taboolib.platform.util.isNotAir

/**
 * @author sky
 * @since 2020-04-20 12:37
 */
internal object ItemListener {

    @Schedule(period = 100, async = true)
    fun onAsyncTick() {
        Bukkit.getOnlinePlayers().forEach {
            it.inventory.filter { item -> item.isNotAir() }.forEach { item ->
                val event = ItemEvent.AsyncTick(item.toItemStream(), it)
                event.call()
                if (event.save) {
                    event.itemStream.rebuildToItemStack(it)
                }
            }
        }
    }

    @SubscribeEvent
    fun onBuildPre(e: ItemBuildEvent.Pre) {
        e.itemStream.getZaphkielItem().invokeScript("onBuild", e.player, e, e.itemStream, "zaphkiel-build")
    }

    @SubscribeEvent
    fun onRelease(e: ItemReleaseEvent) {
        e.itemStream.getZaphkielItem().invokeScript("onRelease", e.player, e, e.itemStream, "zaphkiel-build")
    }

    @SubscribeEvent
    fun onReleaseDisplay(e: ItemReleaseEvent.Display) {
        e.itemStream.getZaphkielItem().invokeScript("onReleaseDisplay", e.player, e, e.itemStream, "zaphkiel-build")
    }

    @SubscribeEvent
    fun onAttack(e: EntityDamageByEntityEvent) {
        val attacker = e.attacker
        if (attacker is Player && attacker.itemInHand.isNotAir()) {
            val itemStream = attacker.itemInHand.toItemStream()
            if (itemStream.isExtension()) {
                itemStream.checkExpiry(attacker)
                itemStream.getZaphkielItem().invokeScript("onAttack", attacker, e, itemStream)
            }
        }
    }

    @SubscribeEvent
    fun onJoin(e: PlayerJoinEvent) {
        e.player.onSelect()
    }

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onChangeWorld(e: PlayerChangedWorldEvent) {
        e.player.onSelect()
    }

    /**
     * 当玩家物品发生损坏时
     * 触发事件
     */
    @SubscribeEvent(ignoreCancelled = true)
    fun onBreak(e: PlayerItemBreakEvent) {
        val itemStream = e.brokenItem.toItemStream()
        if (itemStream.isExtension()) {
            itemStream.checkExpiry(e.player)
            itemStream.getZaphkielItem().invokeScript("onItemBreak", e, itemStream)
        }
    }

    /**
     * 当玩家消耗物品时
     * 触发事件及脚本
     */
    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onConsume(e: PlayerItemConsumeEvent) {
        val itemStack = e.item
        if (itemStack.isAir()) {
            return
        }
        val itemStream = itemStack.toItemStream()
        if (itemStream.isExtension()) {
            itemStream.checkExpiry(e.player)
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
    fun onInteract(e: PlayerInteractEvent) {
        if (e.item.isAir()) {
            return
        }
        val itemStream = e.item!!.toItemStream()
        if (itemStream.isVanilla()) {
            return
        }
        itemStream.checkExpiry(e.player)
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
    fun onInteractAtEntity(e: PlayerInteractEntityEvent) {
        if (e.player.inventory.itemInMainHand.isNotAir() && e.hand == EquipmentSlot.HAND) {
            val itemStream = e.player.inventory.itemInMainHand.toItemStream()
            if (itemStream.isVanilla()) {
                return
            }
            itemStream.checkExpiry(e.player)
            itemStream.getZaphkielUniqueData()
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
    fun onSwap(e: PlayerSwapHandItemsEvent) {
        if (e.offHandItem.isNotAir()) {
            val itemStream = e.offHandItem!!.toItemStream()
            if (itemStream.isExtension()) {
                // 检查过期
                itemStream.checkExpiry(e.player)
                itemStream.getZaphkielItem().invokeScript("onSwapToOffhand", e, itemStream)
            }
        }
        if (e.mainHandItem.isNotAir()) {
            val itemStream = e.mainHandItem!!.toItemStream()
            if (itemStream.isExtension()) {
                // 检查过期
                itemStream.checkExpiry(e.player)
                itemStream.getZaphkielItem().invokeScript("onSwapToMainHand", e, itemStream)
            }
        }
    }

    /**
     * 当玩家破坏方块时
     * 触发脚本
     */
    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBreak(e: BlockBreakEvent) {
        if (e.player.inventory.itemInMainHand.isAir()) {
            return
        }
        val itemStream = e.player.inventory.itemInMainHand.toItemStream()
        if (itemStream.isExtension()) {

            // 检查过期
            itemStream.checkExpiry(e.player)

            itemStream.getZaphkielItem().invokeScript("onBlockBreak", e.player, e, itemStream)
        }
    }

    /**
     * 当玩家破坏方块时（Sandalphon）
     * 触发脚本
     */
    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBreak(e: ink.ptms.sandalphon.module.impl.blockmine.event.BlockBreakEvent) {
        if (e.player.inventory.itemInMainHand.isAir()) {
            return
        }
        val itemStream = e.player.inventory.itemInMainHand.toItemStream()
        if (itemStream.isExtension()) {
            // 检查过期物品
            itemStream.checkExpiry(e.player)

            itemStream.getZaphkielItem().invokeScript("onBlockBreak", e.player, e, itemStream)
        }
    }

    /**
     * 当玩家丢弃物品时
     * 触发事件及脚本
     */
    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onDrop(e: PlayerDropItemEvent) {
        if (e.itemDrop.itemStack.isNotAir()) {
            val itemStream = e.itemDrop.itemStack.toItemStream()
            if (itemStream.isVanilla()) {
                return
            }
            // 检查过期
            itemStream.checkExpiry(e.player)

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
    fun onPickup(e: PlayerPickupItemEvent) {
        if (e.item.itemStack.isNotAir()) {
            val itemStream = e.item.itemStack.toItemStream()
            if (itemStream.isVanilla()) {
                return
            }
            // 检查过期
            itemStream.checkExpiry(e.player)

            val event = ItemEvent.Pick(itemStream, e)
            event.call()
            if (event.save) {
                e.item.setItemStack(event.itemStream.rebuildToItemStack(e.player))
            }
            // 若脚本修改物品则写回事件
            itemStream.getZaphkielItem().invokeScript("onPick", e.player, e, itemStream)?.thenAccept {
                if (it != null) {
                    e.item.itemStack = it.itemStack
                }
            }
        }
    }

    /**
     * 当玩家在背包中点击物品时
     * 触发事件
     */
    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onClick(e: InventoryClickEvent) {
        val itemStreamCurrent = if (e.currentItem.isNotAir()) e.currentItem!!.toItemStream() else null
        var itemStreamButton: ItemStream? = null
        if (e.click == ClickType.NUMBER_KEY) {
            val hotbarButton = e.whoClicked.inventory.getItem(e.hotbarButton)
            if (hotbarButton.isNotAir()) {
                itemStreamButton = hotbarButton!!.toItemStream()
            }
        }
        if (itemStreamCurrent == null && itemStreamButton == null) {
            return
        }
        val event = ItemEvent.InventoryClick(itemStreamCurrent, itemStreamButton, e)
        if (event.call()) {
            if (event.saveCurrent && itemStreamCurrent != null) {
                itemStreamCurrent.rebuildToItemStack(e.whoClicked as Player)
            }
            if (event.saveButton && itemStreamButton != null) {
                itemStreamButton.rebuildToItemStack(e.whoClicked as Player)
            }
        }
    }

    private fun Player.onSelect() {
        inventory.filter { it.isNotAir() }.forEach {
            val event = ItemEvent.Select(it.toItemStream(), this)
            event.call()
            if (event.save) {
                event.itemStream.rebuildToItemStack(this@onSelect)
            }
        }
    }
    private fun ItemStream.checkExpiry(player: Player) {
        if (isExtension()) {
            getZaphkielCompound()?.let {
                val b = it["time"].toString().replace("L","").toLong()
                if (System.currentTimeMillis() >= b) {
                    player.inventory.remove(this.sourceItem)
                }
            }
        }
    }
    private fun ItemStack.checkExpiry() {
        if (isNotAir()) {
            getItemTag()["zaphkiel"]?.let { a ->
                val b = a.asCompound()["time"].toString().replace("L","").toLong()
                if (System.currentTimeMillis() >= b) {
                    type = Material.AIR
                }
            }
        }
    }
    fun Player.checkExpiry() {
        inventory.forEachIndexed { index, item ->
            if (item.isNotAir()) {
                item.getItemTag()["zaphkiel"]?.let { a ->
                    val b = a.asCompound()["time"].toString().replace("L","").toLong()
                    if (System.currentTimeMillis() >= b) {
                        this.inventory.setItem(index, null)
                    }
                }
            }
        }
    }
}