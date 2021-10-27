package ink.ptms.zaphkiel.item.meta

import ink.ptms.zaphkiel.ZaphkielAPI
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.meta.ItemMeta
import taboolib.library.configuration.ConfigurationSection
import java.util.*

@MetaKey("itemflag")
class MetaItemFlag(root: ConfigurationSection) : Meta(root) {

    val itemflag = root.getStringList("meta.itemflag")
        .mapNotNull { ZaphkielAPI.asItemFlag(it.toString().uppercase(Locale.getDefault())) }
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