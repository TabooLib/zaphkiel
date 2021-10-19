package ink.ptms.zaphkiel.item.meta

import ink.ptms.zaphkiel.api.event.ItemReleaseEvent
import taboolib.library.configuration.ConfigurationSection

@MetaKey("data")
class MetaData(root: ConfigurationSection) : Meta(root) {

    val data = root.getInt("meta.data")

    override fun build(itemReleaseEvent: ItemReleaseEvent) {
        itemReleaseEvent.data = data
    }

    override fun drop(itemReleaseEvent: ItemReleaseEvent) {
        itemReleaseEvent.data = 0
    }

    override fun toString(): String {
        return "MetaData(data=$data)"
    }
}