package ink.ptms.zaphkiel.module.meta

import io.izzel.taboolib.util.item.Items
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.meta.ItemMeta

@MetaKey("itemflag")
class MetaItemFlag(root: ConfigurationSection) : Meta(root) {

    val itemflag = root.getStringList("meta.itemflag")
        .mapNotNull { Items.asItemFlag(it.toString().toUpperCase()) }
        .toSet()
        .toTypedArray()

    override fun build(itemMeta: ItemMeta) {
        itemMeta.addItemFlags(*itemflag)
    }

    override fun drop(itemMeta: ItemMeta) {
        itemMeta.removeItemFlags(*ItemFlag.values())
    }

    override fun toString(): String {
        return "MetaItemflag(itemflag=${itemflag.contentToString()})"
    }
}