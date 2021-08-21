package ink.ptms.zaphkiel.module.meta

import taboolib.library.configuration.ConfigurationSection
import org.bukkit.inventory.meta.ItemMeta

@MetaKey("custom-model-data")
class MetaCustomModelData(root: ConfigurationSection) : Meta(root) {

    val data = root.getInt("meta.custom-model-data")

    override fun build(itemMeta: ItemMeta) {
        itemMeta.setCustomModelData(data)
    }

    override fun drop(itemMeta: ItemMeta) {
        itemMeta.setCustomModelData(null)
    }

    override fun toString(): String {
        return "MetaCustomModel(data=$data)"
    }
}