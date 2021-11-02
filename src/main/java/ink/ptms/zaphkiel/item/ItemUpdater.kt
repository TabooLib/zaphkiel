package ink.ptms.zaphkiel.item

import ink.ptms.zaphkiel.ZaphkielAPI
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
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit

/**
 * @author sky
 * @since 2019-12-16 10:40
 */
internal object ItemUpdater {

    @Schedule(period = 100, async = true)
    fun tick() {
        Bukkit.getOnlinePlayers().forEach { player -> ZaphkielAPI.checkUpdate(player, player.inventory) }
    }

    @SubscribeEvent
    fun e(e: PlayerJoinEvent) {
        ZaphkielAPI.checkUpdate(e.player, e.player.inventory)
    }

    @SubscribeEvent
    fun e(e: PlayerRespawnEvent) {
        ZaphkielAPI.checkUpdate(e.player, e.player.inventory)
    }

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: PlayerDropItemEvent) {
        val itemStream = ZaphkielAPI.checkUpdate(e.player, e.itemDrop.itemStack)
        if (ItemSignal.UPDATE_CHECKED in itemStream.signal) {
            e.itemDrop.itemStack = itemStream.toItemStack(e.player)
        }
    }

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: PlayerPickupItemEvent) {
        val itemStream = ZaphkielAPI.checkUpdate(e.player, e.item.itemStack)
        if (ItemSignal.UPDATE_CHECKED in itemStream.signal) {
            e.item.itemStack = itemStream.toItemStack(e.player)
        }
    }

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: InventoryOpenEvent) {
        if (e.inventory.location != null) {
            submit(async = true) {
                ZaphkielAPI.checkUpdate(e.player as Player, e.inventory)
            }
        }
    }
}