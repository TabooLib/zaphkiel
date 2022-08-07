package ink.ptms.zaphkiel.impl.meta

import ink.ptms.zaphkiel.api.event.ItemReleaseEvent
import ink.ptms.zaphkiel.item.meta.Meta
import org.bukkit.Material
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.xseries.parseToMaterial

@MetaKey("icon")
class MetaIcon(root: ConfigurationSection) : Meta(root) {

    val icon = root.getString("meta.icon")?.run { parseToMaterial() } ?: Material.STONE

    override val id: String
        get() = "icon"

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