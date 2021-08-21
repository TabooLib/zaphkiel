package ink.ptms.zaphkiel.module.meta

import ink.ptms.zaphkiel.api.event.ItemReleaseEvent
import org.bukkit.Material
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.xseries.parseToMaterial

@MetaKey("icon")
class MetaIcon(root: ConfigurationSection) : Meta(root) {

    val icon = root.getString("meta.icon")?.run { parseToMaterial() } ?: Material.STONE

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