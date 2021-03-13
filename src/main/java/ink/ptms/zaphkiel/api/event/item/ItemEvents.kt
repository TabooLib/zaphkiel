package ink.ptms.atlantis.api

import ink.ptms.zaphkiel.api.ItemStream
import io.izzel.taboolib.module.event.EventNormal
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemConsumeEvent

/**
 * @Author sky
 * @Since 2020-04-20 12:37
 */
class ItemEvents {

    class InventoryClick(val itemStreamCurrent: ItemStream?, val itemStreamButton: ItemStream?, val bukkitEvent: InventoryClickEvent) : EventNormal<InventoryClick>() {

        var saveCurrent = false
        var saveButton = false
    }

    class InteractEntity(val itemStream: ItemStream, val bukkitEvent: PlayerInteractEntityEvent) : EventNormal<InteractEntity>() {

        var save = false
    }

    class Interact(val itemStream: ItemStream, val bukkitEvent: PlayerInteractEvent) : EventNormal<Interact>() {

        var save = false
    }

    class Consume(val itemStream: ItemStream, val bukkitEvent: PlayerItemConsumeEvent) : EventNormal<Consume>() {

        var save = false
    }

    class Pick(val itemStream: ItemStream, val bukkitEvent: EntityPickupItemEvent) : EventNormal<Pick>() {

        var save = false
        val player = bukkitEvent.entity as Player
    }

    class Drop(val itemStream: ItemStream, val bukkitEvent: PlayerDropItemEvent) : EventNormal<Drop>() {

        var save = false
    }

    class Select(val itemStream: ItemStream, val player: Player) : EventNormal<Select>() {

        var save = false
    }

    class AsyncTick(val itemStream: ItemStream, val player: Player) : EventNormal<AsyncTick>() {

        init {
            async(true)
        }

        var save = false
    }
}