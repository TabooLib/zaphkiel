package ink.ptms.zaphkiel.impl.meta

import ink.ptms.zaphkiel.item.meta.Meta
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.meta.ItemMeta
import taboolib.library.configuration.ConfigurationSection

@MetaKey("itemflag")
class MetaItemFlag(root: ConfigurationSection) : Meta(root) {

    val itemflag = root.getStringList("meta.itemflag")
        .mapNotNull { kotlin.runCatching { ItemFlag.valueOf(it.uppercase()) }.getOrNull() }
        .toSet()
        .toTypedArray()

    override val id: String
        get() = "itemflag"

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