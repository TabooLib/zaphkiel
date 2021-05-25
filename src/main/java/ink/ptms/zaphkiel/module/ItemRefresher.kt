package ink.ptms.zaphkiel.module

import ink.ptms.zaphkiel.Zaphkiel
import ink.ptms.zaphkiel.ZaphkielAPI
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.module.inject.TSchedule
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import org.bukkit.event.player.PlayerRespawnEvent

/**
 * @Author sky
 * @Since 2019-12-16 10:40
 */
@TListener
private class ItemRefresher : Listener {

    @EventHandler
    fun e(e: PlayerJoinEvent) {
        ZaphkielAPI.rebuild(e.player, e.player.inventory)
    }

    @EventHandler
    fun e(e: PlayerRespawnEvent) {
        ZaphkielAPI.rebuild(e.player, e.player.inventory)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: PlayerDropItemEvent) {
        ZaphkielAPI.rebuild(e.player, e.itemDrop.itemStack).run {
            if (this.rebuild) {
                e.itemDrop.setItemStack(this.save())
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: PlayerPickupItemEvent) {
        ZaphkielAPI.rebuild(e.player, e.item.itemStack).run {
            if (this.rebuild) {
                e.item.setItemStack(this.save())
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: InventoryOpenEvent) {
        if (e.inventory.location != null) {
            Bukkit.getScheduler().runTaskAsynchronously(Zaphkiel.getPlugin(), Runnable {
                ZaphkielAPI.rebuild(e.player as Player, e.inventory)
            })
        }
    }

    companion object {

        @TSchedule(period = 100, async = true)
        fun tick() {
            Bukkit.getOnlinePlayers().forEach { player -> ZaphkielAPI.rebuild(player, player.inventory) }
        }
    }
}