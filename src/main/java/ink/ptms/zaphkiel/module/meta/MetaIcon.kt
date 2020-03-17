package ink.ptms.zaphkiel.module.meta

import ink.ptms.zaphkiel.api.Item
import ink.ptms.zaphkiel.api.event.single.ItemReleaseEvent
import io.izzel.taboolib.util.item.Items

@MetaKey("icon")
class MetaIcon(item: Item) : Meta(item) {

    val icon = Items.asMaterial(item.config.getString("meta.icon"))

    override fun build(itemReleaseEvent: ItemReleaseEvent) {
        itemReleaseEvent.icon = icon
    }

    override fun toString(): String {
        return "MetaIcon(icon=$icon)"
    }
}