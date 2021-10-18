package ink.ptms.zaphkiel.module

import ink.ptms.zaphkiel.ZaphkielAPI
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
 * @Author sky
 * @Since 2019-12-16 10:40
 */
internal object ItemRefresher {

    @Schedule(period = 100, async = true)
    fun tick() {
        Bukkit.getOnlinePlayers().forEach { player -> ZaphkielAPI.rebuild(player, player.inventory) }
    }

    @SubscribeEvent
    fun e(e: PlayerJoinEvent) {
        ZaphkielAPI.rebuild(e.player, e.player.inventory)
    }

    @SubscribeEvent
    fun e(e: PlayerRespawnEvent) {
        ZaphkielAPI.rebuild(e.player, e.player.inventory)
    }

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: PlayerDropItemEvent) {
        ZaphkielAPI.rebuild(e.player, e.itemDrop.itemStack).run {
            if (this.rebuild) {
                e.itemDrop.itemStack = this.saveNow()
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: PlayerPickupItemEvent) {
        ZaphkielAPI.rebuild(e.player, e.item.itemStack).run {
            if (this.rebuild) {
                e.item.itemStack = this.saveNow()
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: InventoryOpenEvent) {
        if (e.inventory.location != null) {
            submit(async = true) {
                ZaphkielAPI.rebuild(e.player as Player, e.inventory)
            }
        }
    }
}