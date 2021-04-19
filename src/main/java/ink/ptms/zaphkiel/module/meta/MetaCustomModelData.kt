package ink.ptms.zaphkiel.module.meta

import ink.ptms.zaphkiel.api.Item
import org.bukkit.inventory.meta.ItemMeta

@MetaKey("custom-model-data")
class MetaCustomModelData(item: Item) : Meta(item) {

    val data = item.config.getInt("meta.custom-model-data")

    override fun build(itemMeta: ItemMeta) {
        itemMeta.setCustomModelData(data)
    }

    override fun toString(): String {
        return "MetaCustomModel(data=$data)"
    }
}