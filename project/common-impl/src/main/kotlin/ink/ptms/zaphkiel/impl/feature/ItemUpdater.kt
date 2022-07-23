package ink.ptms.zaphkiel.impl.feature

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
import taboolib.common.platform.function.submit

/**
 * @author sky
 * @since 2019-12-16 10:40
 */
object ItemUpdater {

    @Schedule(period = 100, async = true)
    fun tick() {
        Bukkit.getOnlinePlayers().forEach { player -> Zaphkiel.api().getItemUpdater().checkUpdate(player, player.inventory) }
    }

    @SubscribeEvent
    fun e(e: PlayerJoinEvent) {
        Zaphkiel.api().getItemUpdater().checkUpdate(e.player, e.player.inventory)
    }

    @SubscribeEvent
    fun e(e: PlayerRespawnEvent) {
        Zaphkiel.api().getItemUpdater().checkUpdate(e.player, e.player.inventory)
    }

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: PlayerDropItemEvent) {
        val itemStream = Zaphkiel.api().getItemUpdater().checkUpdate(e.player, e.itemDrop.itemStack)
        if (ItemSignal.UPDATE_CHECKED in itemStream.signal) {
            e.itemDrop.setItemStack(itemStream.toItemStack(e.player))
        }
    }

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: PlayerPickupItemEvent) {
        val itemStream = Zaphkiel.api().getItemUpdater().checkUpdate(e.player, e.item.itemStack)
        if (ItemSignal.UPDATE_CHECKED in itemStream.signal) {
            e.item.setItemStack(itemStream.toItemStack(e.player))
        }
    }

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: InventoryOpenEvent) {
        kotlin.runCatching {
            if (e.inventory.location != null) {
                submit(async = true) {
                    Zaphkiel.api().getItemUpdater().checkUpdate(e.player as Player, e.inventory)
                }
            }
        }
    }

    @SubscribeEvent(bind = "cc.bukkitPlugin.pds.events.PlayerDataLoadCompleteEvent")
    fun event(e: OptionalEvent) {
        val player = e.read<Player>("player")!!
        Zaphkiel.api().getItemUpdater().checkUpdate(player, player.inventory)
    }
}