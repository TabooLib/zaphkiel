package ink.ptms.zaphkiel.module.meta

import ink.ptms.zaphkiel.api.Item
import io.izzel.taboolib.util.item.Items
import org.bukkit.inventory.meta.ItemMeta

@MetaKey("custommodel")
class MetaCustomModel(item: Item) : Meta(item) {

    val custommodel = item.config.getInt("meta.custommodel")

    override fun build(itemMeta: ItemMeta) {
        itemMeta.setCustomModelData(custommodel)
    }

    override fun toString(): String {
        return "MetaCustomModel(custommodel=$custommodel)"
    }
}