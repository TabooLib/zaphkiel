package ink.ptms.zaphkiel.module.meta

import ink.ptms.zaphkiel.api.event.single.ItemReleaseEvent
import io.izzel.taboolib.util.item.Items
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection

@MetaKey("icon")
class MetaIcon(root: ConfigurationSection) : Meta(root) {

    val icon = root.getString("meta.icon")?.run { Items.asMaterial(this) } ?: Material.STONE

    override fun build(itemReleaseEvent: ItemReleaseEvent) {
        itemReleaseEvent.icon = icon
    }

    override fun drop(itemReleaseEvent: ItemReleaseEvent) {
        itemReleaseEvent.icon = itemReleaseEvent.item.icon.type
    }

    override fun toString(): String {
        return "MetaIcon(icon=$icon)"
    }
}