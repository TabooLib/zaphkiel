package ink.ptms.zaphkiel.impl.meta

import ink.ptms.tiphareth.TipharethAPI
import ink.ptms.zaphkiel.api.event.ItemReleaseEvent
import ink.ptms.zaphkiel.item.meta.Meta
import taboolib.library.configuration.ConfigurationSection

@Suppress("SpellCheckingInspection")
@MetaKey("tiphareth")
class MetaTiphareth(root: ConfigurationSection) : Meta(root) {

    val tiphareth = root.getString("meta.tiphareth")?.run { TipharethAPI.LOADER.getByName(this)?.buildItem() }

    override val id: String
        get() = "tiphareth"

    override fun build(itemReleaseEvent: ItemReleaseEvent) {
        if (tiphareth != null) {
            itemReleaseEvent.icon = tiphareth.type
            itemReleaseEvent.itemMeta.setCustomModelData(tiphareth.itemMeta!!.customModelData)
        }
    }

    override fun drop(itemReleaseEvent: ItemReleaseEvent) {
        itemReleaseEvent.icon = itemReleaseEvent.item.icon.type
        itemReleaseEvent.itemMeta.setCustomModelData(null)
    }

    override fun toString(): String {
        return "MetaTiphareth(tiphareth=$tiphareth)"
    }
}