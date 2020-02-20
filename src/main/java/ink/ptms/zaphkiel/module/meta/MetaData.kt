package ink.ptms.zaphkiel.module.meta

import ink.ptms.zaphkiel.api.Item
import ink.ptms.zaphkiel.api.event.ItemReleaseEvent

@MetaKey("data")
class MetaData(item: Item) : Meta(item) {

    val data = item.config.getInt("meta.data")

    override fun build(itemReleaseEvent: ItemReleaseEvent) {
        itemReleaseEvent.data = data
    }

    override fun toString(): String {
        return "MetaData(data=$data)"
    }
}