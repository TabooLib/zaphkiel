package ink.ptms.zaphkiel.api.event

import ink.ptms.zaphkiel.api.Item
import ink.ptms.zaphkiel.api.ItemStream
import io.izzel.taboolib.module.event.EventCancellable
import org.bukkit.Bukkit
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2019-12-16 10:46
 */
class ItemRebuildEvent(val player: Player?, val itemStream: ItemStream, refresh: Boolean) : EventCancellable<ItemRebuildEvent>() {

    init {
        isCancelled = !refresh
        async(!Bukkit.isPrimaryThread())
    }
}