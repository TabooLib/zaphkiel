package ink.ptms.zaphkiel.item.meta

import org.bukkit.inventory.meta.ItemMeta
import taboolib.library.configuration.ConfigurationSection

@MetaKey("custommodel")
@Deprecated("legacy")
class MetaCustomModel(root: ConfigurationSection) : Meta(root) {

    val custommodel = root.getInt("meta.custommodel")

    override fun build(itemMeta: ItemMeta) {
        itemMeta.setCustomModelData(custommodel)
    }

    override fun drop(itemMeta: ItemMeta) {
        itemMeta.setCustomModelData(null)
    }

    override fun toString(): String {
        return "MetaCustomModel(custommodel=$custommodel)"
    }
}