package ink.ptms.zaphkiel.module.meta

import ink.ptms.zaphkiel.api.Item
import io.izzel.taboolib.util.item.Items
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta

@MetaKey("itemflag")
class MetaItemflag(item: Item) : Meta(item) {

    val itemflag = item.config.getStringList("meta.itemflag").map { Items.asItemFlag(it) }.filterNotNull().toTypedArray()

    override fun build(itemMeta: ItemMeta) {
        itemMeta.addItemFlags(*itemflag)
    }
}