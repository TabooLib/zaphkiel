package ink.ptms.zaphkiel.module.meta

import ink.ptms.zaphkiel.api.Item
import io.izzel.taboolib.util.item.Items
import org.bukkit.inventory.meta.ItemMeta

@MetaKey("unbreakable")
class MetaUnbreakable(item: Item) : Meta(item) {

    val unbreakable = item.config.getBoolean("meta.unbreakable")

    override fun build(itemMeta: ItemMeta) {
        itemMeta.isUnbreakable = unbreakable
    }

    override fun toString(): String {
        return "MetaUnbreakable(unbreakable=$unbreakable)"
    }
}