package ink.ptms.zaphkiel.impl.internal

import ink.ptms.zaphkiel.Zaphkiel
import ink.ptms.zaphkiel.api.ItemSignal
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import org.bukkit.event.player.PlayerRespawnEvent
import taboolib.common.platform.Schedule
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.OptionalEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submitAsync

/**
 * @author sky
 * @since 2019-12-16 10:40
 */
private object ItemUpdater {

    /**
     * 每 100 tick 更新一次背包（异步更新）
     */
    @Schedule(period = 100, async = true)
    fun tick() {
        Bukkit.getOnlinePlayers().forEach { player -> Zaphkiel.api().getItemUpdater().checkUpdate(player, player.inventory) }
    }

    /**
     * 进入游戏时更新背包（同步更新）
     */
    @SubscribeEvent
    fun onJoin(e: PlayerJoinEvent) {
        Zaphkiel.api().getItemUpdater().checkUpdate(e.player, e.player.inventory)
    }

    /**
     * 复活时更新背包（同步更新）
     */
    @SubscribeEvent
    fun onRespawn(e: PlayerRespawnEvent) {
        Zaphkiel.api().getItemUpdater().checkUpdate(e.player, e.player.inventory)
    }

    /**
     * 丢弃物品时更新物品（同步更新）
     */
    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onDrop(e: PlayerDropItemEvent) {
        val itemStream = Zaphkiel.api().getItemUpdater().checkUpdate(e.player, e.itemDrop.itemStack)
        if (ItemSignal.UPDATE_CHECKED in itemStream.signal) {
            e.itemDrop.itemStack = itemStream.toItemStack(e.player)
        }
    }

    /**
     * 捡起物品时更新物品（同步更新）
     */
    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPickup(e: PlayerPickupItemEvent) {
        val itemStream = Zaphkiel.api().getItemUpdater().checkUpdate(e.player, e.item.itemStack)
        if (ItemSignal.UPDATE_CHECKED in itemStream.signal) {
            e.item.itemStack = itemStream.toItemStack(e.player)
        }
    }

    /**
     * 打开箱子时更新物品（异步更新）
     */
    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onOpen(e: InventoryOpenEvent) {
        kotlin.runCatching {
            if (e.inventory.location != null) {
                submitAsync { Zaphkiel.api().getItemUpdater().checkUpdate(e.player as Player, e.inventory) }
            }
        }
    }

    /**
     * 读取玩家数据时更新背包（同步更新）
     */
    @SubscribeEvent(bind = "cc.bukkitPlugin.pds.events.PlayerDataLoadCompleteEvent")
    fun onLoad(e: OptionalEvent) {
        val player = e.read<Player>("player")!!
        Zaphkiel.api().getItemUpdater().checkUpdate(player, player.inventory)
    }
}