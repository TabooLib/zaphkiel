package ink.ptms.zaphkiel.module.meta

import ink.ptms.zaphkiel.api.Item
import io.izzel.taboolib.util.item.Items
import org.bukkit.inventory.meta.ItemMeta
import java.util.*

@MetaKey("itemflag")
class MetaItemflag(item: Item) : Meta(item) {

    val itemflag = item.config.getStringList("meta.itemflag").mapNotNull { Items.asItemFlag(it.toString().toUpperCase()) }.toTypedArray()

    override fun build(itemMeta: ItemMeta) {
        itemMeta.addItemFlags(*itemflag)
    }

    override fun toString(): String {
        return "MetaItemflag(itemflag=${itemflag.contentToString()})"
    }
}