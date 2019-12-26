package ink.ptms.zaphkiel.api.event

import ink.ptms.zaphkiel.api.ItemStream
import io.izzel.taboolib.module.event.EventNormal
import org.bukkit.Bukkit
import org.bukkit.inventory.meta.ItemMeta

/**
 * @Author sky
 * @Since 2019-12-25 11:38
 */
class ItemReleaseEvent(var itemMeta: ItemMeta, val itemStream: ItemStream) : EventNormal<ItemReleaseEvent>() {

    val item = itemStream.getZaphkielItem()

    init {
        async(!Bukkit.isPrimaryThread())
    }
}